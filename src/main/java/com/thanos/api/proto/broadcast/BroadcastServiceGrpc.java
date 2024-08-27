package com.thanos.api.proto.broadcast;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 *服务接口定义，服务端和客户端都要遵循该接口进行通信
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.30.2)",
    comments = "Source: broadcast.proto")
public final class BroadcastServiceGrpc {

  private BroadcastServiceGrpc() {}

  public static final String SERVICE_NAME = "com.thanos.api.proto.broadcast.BroadcastService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO,
      com.thanos.api.proto.broadcast.DefaultResponse> getTransportEthTransactionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transportEthTransactions",
      requestType = com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO.class,
      responseType = com.thanos.api.proto.broadcast.DefaultResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO,
      com.thanos.api.proto.broadcast.DefaultResponse> getTransportEthTransactionsMethod() {
    io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO, com.thanos.api.proto.broadcast.DefaultResponse> getTransportEthTransactionsMethod;
    if ((getTransportEthTransactionsMethod = BroadcastServiceGrpc.getTransportEthTransactionsMethod) == null) {
      synchronized (BroadcastServiceGrpc.class) {
        if ((getTransportEthTransactionsMethod = BroadcastServiceGrpc.getTransportEthTransactionsMethod) == null) {
          BroadcastServiceGrpc.getTransportEthTransactionsMethod = getTransportEthTransactionsMethod =
              io.grpc.MethodDescriptor.<com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO, com.thanos.api.proto.broadcast.DefaultResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "transportEthTransactions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.DefaultResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BroadcastServiceMethodDescriptorSupplier("transportEthTransactions"))
              .build();
        }
      }
    }
    return getTransportEthTransactionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO,
      com.thanos.api.proto.broadcast.DefaultResponse> getTransportGlobalNodeEventsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "transportGlobalNodeEvents",
      requestType = com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO.class,
      responseType = com.thanos.api.proto.broadcast.DefaultResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO,
      com.thanos.api.proto.broadcast.DefaultResponse> getTransportGlobalNodeEventsMethod() {
    io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO, com.thanos.api.proto.broadcast.DefaultResponse> getTransportGlobalNodeEventsMethod;
    if ((getTransportGlobalNodeEventsMethod = BroadcastServiceGrpc.getTransportGlobalNodeEventsMethod) == null) {
      synchronized (BroadcastServiceGrpc.class) {
        if ((getTransportGlobalNodeEventsMethod = BroadcastServiceGrpc.getTransportGlobalNodeEventsMethod) == null) {
          BroadcastServiceGrpc.getTransportGlobalNodeEventsMethod = getTransportGlobalNodeEventsMethod =
              io.grpc.MethodDescriptor.<com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO, com.thanos.api.proto.broadcast.DefaultResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "transportGlobalNodeEvents"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.DefaultResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BroadcastServiceMethodDescriptorSupplier("transportGlobalNodeEvents"))
              .build();
        }
      }
    }
    return getTransportGlobalNodeEventsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.NodesBroadcastDTO,
      com.thanos.api.proto.broadcast.NodesBroadcastDTO> getFindNodesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "findNodes",
      requestType = com.thanos.api.proto.broadcast.NodesBroadcastDTO.class,
      responseType = com.thanos.api.proto.broadcast.NodesBroadcastDTO.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.NodesBroadcastDTO,
      com.thanos.api.proto.broadcast.NodesBroadcastDTO> getFindNodesMethod() {
    io.grpc.MethodDescriptor<com.thanos.api.proto.broadcast.NodesBroadcastDTO, com.thanos.api.proto.broadcast.NodesBroadcastDTO> getFindNodesMethod;
    if ((getFindNodesMethod = BroadcastServiceGrpc.getFindNodesMethod) == null) {
      synchronized (BroadcastServiceGrpc.class) {
        if ((getFindNodesMethod = BroadcastServiceGrpc.getFindNodesMethod) == null) {
          BroadcastServiceGrpc.getFindNodesMethod = getFindNodesMethod =
              io.grpc.MethodDescriptor.<com.thanos.api.proto.broadcast.NodesBroadcastDTO, com.thanos.api.proto.broadcast.NodesBroadcastDTO>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "findNodes"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.NodesBroadcastDTO.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.thanos.api.proto.broadcast.NodesBroadcastDTO.getDefaultInstance()))
              .setSchemaDescriptor(new BroadcastServiceMethodDescriptorSupplier("findNodes"))
              .build();
        }
      }
    }
    return getFindNodesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BroadcastServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceStub>() {
        @java.lang.Override
        public BroadcastServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BroadcastServiceStub(channel, callOptions);
        }
      };
    return BroadcastServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BroadcastServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceBlockingStub>() {
        @java.lang.Override
        public BroadcastServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BroadcastServiceBlockingStub(channel, callOptions);
        }
      };
    return BroadcastServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BroadcastServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BroadcastServiceFutureStub>() {
        @java.lang.Override
        public BroadcastServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BroadcastServiceFutureStub(channel, callOptions);
        }
      };
    return BroadcastServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *服务接口定义，服务端和客户端都要遵循该接口进行通信
   * </pre>
   */
  public static abstract class BroadcastServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     *接收请求，返回响应
     * </pre>
     */
    public void transportEthTransactions(com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransportEthTransactionsMethod(), responseObserver);
    }

    /**
     */
    public void transportGlobalNodeEvents(com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransportGlobalNodeEventsMethod(), responseObserver);
    }

    /**
     */
    public void findNodes(com.thanos.api.proto.broadcast.NodesBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.NodesBroadcastDTO> responseObserver) {
      asyncUnimplementedUnaryCall(getFindNodesMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getTransportEthTransactionsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO,
                com.thanos.api.proto.broadcast.DefaultResponse>(
                  this, METHODID_TRANSPORT_ETH_TRANSACTIONS)))
          .addMethod(
            getTransportGlobalNodeEventsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO,
                com.thanos.api.proto.broadcast.DefaultResponse>(
                  this, METHODID_TRANSPORT_GLOBAL_NODE_EVENTS)))
          .addMethod(
            getFindNodesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.thanos.api.proto.broadcast.NodesBroadcastDTO,
                com.thanos.api.proto.broadcast.NodesBroadcastDTO>(
                  this, METHODID_FIND_NODES)))
          .build();
    }
  }

  /**
   * <pre>
   *服务接口定义，服务端和客户端都要遵循该接口进行通信
   * </pre>
   */
  public static final class BroadcastServiceStub extends io.grpc.stub.AbstractAsyncStub<BroadcastServiceStub> {
    private BroadcastServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BroadcastServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BroadcastServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     *接收请求，返回响应
     * </pre>
     */
    public void transportEthTransactions(com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransportEthTransactionsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transportGlobalNodeEvents(com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransportGlobalNodeEventsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findNodes(com.thanos.api.proto.broadcast.NodesBroadcastDTO request,
        io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.NodesBroadcastDTO> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindNodesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *服务接口定义，服务端和客户端都要遵循该接口进行通信
   * </pre>
   */
  public static final class BroadcastServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<BroadcastServiceBlockingStub> {
    private BroadcastServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BroadcastServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BroadcastServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     *接收请求，返回响应
     * </pre>
     */
    public com.thanos.api.proto.broadcast.DefaultResponse transportEthTransactions(com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO request) {
      return blockingUnaryCall(
          getChannel(), getTransportEthTransactionsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.thanos.api.proto.broadcast.DefaultResponse transportGlobalNodeEvents(com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO request) {
      return blockingUnaryCall(
          getChannel(), getTransportGlobalNodeEventsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.thanos.api.proto.broadcast.NodesBroadcastDTO findNodes(com.thanos.api.proto.broadcast.NodesBroadcastDTO request) {
      return blockingUnaryCall(
          getChannel(), getFindNodesMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *服务接口定义，服务端和客户端都要遵循该接口进行通信
   * </pre>
   */
  public static final class BroadcastServiceFutureStub extends io.grpc.stub.AbstractFutureStub<BroadcastServiceFutureStub> {
    private BroadcastServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BroadcastServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BroadcastServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     *接收请求，返回响应
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.thanos.api.proto.broadcast.DefaultResponse> transportEthTransactions(
        com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO request) {
      return futureUnaryCall(
          getChannel().newCall(getTransportEthTransactionsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.thanos.api.proto.broadcast.DefaultResponse> transportGlobalNodeEvents(
        com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO request) {
      return futureUnaryCall(
          getChannel().newCall(getTransportGlobalNodeEventsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.thanos.api.proto.broadcast.NodesBroadcastDTO> findNodes(
        com.thanos.api.proto.broadcast.NodesBroadcastDTO request) {
      return futureUnaryCall(
          getChannel().newCall(getFindNodesMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRANSPORT_ETH_TRANSACTIONS = 0;
  private static final int METHODID_TRANSPORT_GLOBAL_NODE_EVENTS = 1;
  private static final int METHODID_FIND_NODES = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BroadcastServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BroadcastServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_TRANSPORT_ETH_TRANSACTIONS:
          serviceImpl.transportEthTransactions((com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO) request,
              (io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse>) responseObserver);
          break;
        case METHODID_TRANSPORT_GLOBAL_NODE_EVENTS:
          serviceImpl.transportGlobalNodeEvents((com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO) request,
              (io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.DefaultResponse>) responseObserver);
          break;
        case METHODID_FIND_NODES:
          serviceImpl.findNodes((com.thanos.api.proto.broadcast.NodesBroadcastDTO) request,
              (io.grpc.stub.StreamObserver<com.thanos.api.proto.broadcast.NodesBroadcastDTO>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BroadcastServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BroadcastServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.thanos.api.proto.broadcast.BroadcastGrpcTransport.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BroadcastService");
    }
  }

  private static final class BroadcastServiceFileDescriptorSupplier
      extends BroadcastServiceBaseDescriptorSupplier {
    BroadcastServiceFileDescriptorSupplier() {}
  }

  private static final class BroadcastServiceMethodDescriptorSupplier
      extends BroadcastServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BroadcastServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BroadcastServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BroadcastServiceFileDescriptorSupplier())
              .addMethod(getTransportEthTransactionsMethod())
              .addMethod(getTransportGlobalNodeEventsMethod())
              .addMethod(getFindNodesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
