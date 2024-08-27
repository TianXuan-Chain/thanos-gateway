package com.thanos.gateway.jsonrpc;

import com.googlecode.jsonrpc4j.JsonRpcBasicServer;
import com.thanos.common.utils.ThanosThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLException;

/**
 * CustomizeStreamServer.java description：
 *
 * @Author laiyiyu create on 2021-02-23 11:20:48
 */
public class CustomizeStreamServer {

    private static final Logger logger = LoggerFactory.getLogger("jsonrpc");

    private static final long SERVER_SOCKET_CONNECT_TIMEOUT = 5000;
    //private static final int SOCKET_READ_WRITE_TIMEOUT = 1000 * 60 * 5;
    private static final int DEFAULT_SOCKET_READ_WRITE_TIMEOUT = 1000 * 60 * 5;
    private static final String STREAM_ENDED_EXCEPTION_NAME = "StreamEndedException";

    private final ThreadPoolExecutor executor;
    private final ServerSocket serverSocket;
    private final JsonRpcBasicServer jsonRpcServer;
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    private final AtomicBoolean keepRunning = new AtomicBoolean(false);
    private final Set<CustomizeStreamServer.Server> servers = new HashSet<>();
    private int maxClientErrors = 5;
    private int socketReadWriteTimeout;

    /**
     * Creates a {@code StreamServer} with the given max number
     * of threads.  A {@link ServerSocket} is created using the
     * default {@link ServerSocketFactory} that lists on the
     * given {@code port} and {@link InetAddress}.
     *
     * @param jsonRpcServer the {@link JsonRpcBasicServer} that will handleRequest requests
     * @param maxThreads the mac number of threads the server will spawn
     * @param port the port to listen on
     * @param backlog the {@link ServerSocket} backlog
     * @param bindAddress the address to listen on
     * @throws IOException on error
     */
    private CustomizeStreamServer(JsonRpcBasicServer jsonRpcServer, int maxThreads, int port, int backlog, InetAddress bindAddress) throws IOException {
        this(jsonRpcServer, maxThreads, DEFAULT_SOCKET_READ_WRITE_TIMEOUT, ServerSocketFactory.getDefault().createServerSocket(port, backlog, bindAddress));
    }

    /**
     * Creates a {@code StreamServer} with the given max number
     * of threads using the given {@link ServerSocket} to listen
     * for client connections.
     *
     * @param jsonRpcServer the {@link JsonRpcBasicServer} that will handleRequest requests
     * @param maxThreads the mac number of threads the server will spawn
     * @param serverSocket the {@link ServerSocket} used for accepting client connections
     */
    public CustomizeStreamServer(JsonRpcBasicServer jsonRpcServer, int maxThreads, int readWriteTimeout, ServerSocket serverSocket) {
        this.jsonRpcServer = jsonRpcServer;
        this.serverSocket = serverSocket;
        socketReadWriteTimeout = readWriteTimeout;
        executor = new ThreadPoolExecutor(maxThreads + 1, maxThreads + 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(maxThreads * 2), new ThanosThreadFactory("json_rpc"));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        jsonRpcServer.setRethrowExceptions(false);
    }

    /**
     * Returns the current servers.
     * @return the servers
     */
    public Set<CustomizeStreamServer.Server> getServers() {
        return Collections.unmodifiableSet(servers);
    }

    /**
     * Starts the server.
     */
    public void start() {
        if (tryToStart()) { throw new IllegalStateException("The StreamServer is already started"); }
        logger.debug("StreamServer starting {}:{}", serverSocket.getInetAddress(), serverSocket.getLocalPort());
        keepRunning.set(true);
        executor.submit(new CustomizeStreamServer.Server());
    }

    private boolean tryToStart() {
        return !isStarted.compareAndSet(false, true);
    }

    /**
     * Stops the server thread.
     * @throws InterruptedException if a graceful shutdown didn't happen
     */
    public void stop() throws InterruptedException {
        if (!isStarted.get()) { throw new IllegalStateException("The StreamServer is not started"); }
        stopServer();
        stopClients();
        closeSocket();
        try {
            waitForServerToTerminate();
            isStarted.set(false);
            stopServer();
        } catch (InterruptedException e) {
            logger.error("InterruptedException while waiting for termination", e);
            throw e;
        }
    }

    private void stopServer() {
        keepRunning.set(false);
    }

    private void stopClients() {
        executor.shutdownNow();
    }

    private void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.debug("Failed to close socket", e);
        }
    }

    private void waitForServerToTerminate() throws InterruptedException {
        if (!executor.isTerminated()) {
            executor.awaitTermination(2000 + SERVER_SOCKET_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Closes something quietly.
     * @param c closable
     */
    private void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable t) {
                logger.warn("Error closing, ignoring", t);
            }
        }
    }

    /**
     * @return the number of connected clients
     */
    public int getNumberOfConnections() {
        return servers.size();
    }

    /**
     * @return the maxClientErrors
     */
    public int getMaxClientErrors() {
        return maxClientErrors;
    }

    /**
     * @param maxClientErrors the maxClientErrors to set
     */
    public void setMaxClientErrors(int maxClientErrors) {
        this.maxClientErrors = maxClientErrors;
    }

    /**
     * @return the isStarted
     */
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * Server thread.
     */
    public class Server implements Runnable {

        private int errors;
        private Throwable lastException;

        public int getNumberOfErrors() {
            return errors;
        }

        public Throwable getLastException() {
            return lastException;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            ServerSocket serverSocket = CustomizeStreamServer.this.serverSocket;
            Socket clientSocket = null;
            while (CustomizeStreamServer.this.keepRunning.get()) {
                try {
                    serverSocket.setSoTimeout((int) SERVER_SOCKET_CONNECT_TIMEOUT);
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(socketReadWriteTimeout);
                    logger.info("Client connected: {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                    // spawn a new Server for the next connection and break out of the server loop
                    break;
                } catch (SocketTimeoutException e) {
                    closeQuietly(clientSocket);
                    handleSocketTimeoutException(e);
                } catch (SSLException sslException) {
                    closeQuietly(clientSocket);
                    logger.debug("SSLException while listening for clients, terminating", sslException);
                    break;
                } catch (IOException ioe) {
                    closeQuietly(clientSocket);
                    // this could be because the ServerSocket was closed
                    if (SocketException.class.isInstance(ioe) && !keepRunning.get()) {
                        return;
                    }
                    logger.debug("Exception while listening for clients", ioe);
                } catch (Throwable e) {
                    closeQuietly(clientSocket);
                    logger.debug("un know error", e);
                }
            }

            try {
                executor.submit(new CustomizeStreamServer.Server());
            } catch (Throwable e) {
            }


            if (clientSocket != null) {
                BufferedInputStream input = null;
                OutputStream output = null;
                try {
                    input = new BufferedInputStream(clientSocket.getInputStream());
                    output = clientSocket.getOutputStream();
                } catch (Throwable e) {
                    logger.debug("Client socket failed", e);
                    //servers.remove(this);
                    closeQuietly(input);
                    closeQuietly(output);
                    closeQuietly(clientSocket);
                    return;
                }


                try {
                    servers.add(this);
                    while (CustomizeStreamServer.this.keepRunning.get()) {
                        try {
                            jsonRpcServer.handleRequest(input, output);
                        } catch (Throwable t) {
                            if (STREAM_ENDED_EXCEPTION_NAME.equals(t.getClass().getSimpleName())) {
                                logger.debug("Client disconnected: {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                                break;
                            }

                            errors++;
                            lastException = t;
                            if (errors < maxClientErrors) {
                                logger.debug("Exception while handling request, {}:{}, error:{}" , clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), ExceptionUtils.getStackTrace(t));
                            } else {
                                logger.debug("Closing client connection due to repeated {}:{}, error:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), ExceptionUtils.getStackTrace(t));
                                break;
                            }
                        }
                    }
                } finally {
                    closeQuietly(input);
                    closeQuietly(output);
                    closeQuietly(clientSocket);
                    servers.remove(this);
                    logger.warn("Client close connected: {}:{}", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                }
            }
        }



        private void handleSocketTimeoutException(SocketTimeoutException e) {
            // this is expected because of so_timeout
        }
    }

    public static void main(String[] args) {

        try {

            throw new IllegalStateException("hehe");

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("exception");
        }

    }
}
