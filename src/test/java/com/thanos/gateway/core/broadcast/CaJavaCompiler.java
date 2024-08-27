package com.thanos.gateway.core.broadcast;


import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.ByteUtil;
import com.thanos.common.utils.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import javax.annotation.concurrent.NotThreadSafe;
import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * CaJavaCompiler.java description：
 *
 * @Author laiyiyu create on 2021-04-15 17:15:32
 */
@NotThreadSafe
public class CaJavaCompiler {

    private static final Logger logger = LoggerFactory.getLogger("ca");

    public static final String JAVA_EXTENSION = ".java";

    private static final JavaCompiler compiler;

    private static final List<String> options;

    private static final ClassLoader parentLoader;

    static {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            logger.error("please copy jdk/lib/tools.jar to the jre/lib/tools.jar");
            System.exit(0);
        }

        options = new ArrayList<String>();
        options.add("-source");
        options.add("1.8");
        options.add("-target");
        options.add("1.8");

        parentLoader = CaJavaCompiler.class.getClassLoader();
    }

    static String clazzName = "com.thanos.gateway.core.broadcast.Son";
    static String code = "package com.thanos.gateway.core.broadcast;\n" +
            "\n" +
            "/**\n" +
            " * 类Son.java的实现描述：\n" +
            " *\n" +
            " * @Author laiyiyu create on 2019-12-13 15:29:49\n" +
            " */\n" +
            "public class Son  {\n" +
            "\n" +
            "    private int temp;\n" +
            "\n" +
            "    //GrandSon grandSon;\n" +
            "\n" +
            "\n" +
            "    public String getName() {\n" +
            "        return \"hhe\";\n" +
            "    }\n" +
            "\n" +
            "    public String getAge() {\n" +
            "        return this.getClass().getSimpleName();\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "//        Son son = new Son();\n" +
            "//        System.out.println(son.get());\n" +
            "//        son.method();\n" +
            "\n" +
            "        System.out.println((1<<11) | (1<<10) | (1<<2));\n" +
            "        System.out.println((1<<(2051%2048 - 1)) | (1<<4) | (1<<10));\n" +
            "        System.out.println(3076|1044);\n" +
            "        System.out.println(12296|2178);\n" +
            "        System.out.println(12296|2178);\n" +
            "\n" +
            "        int i = 0;\n" +
            "        retry:\n" +
            "\n" +
            "        while (i < 4) {\n" +
            "            i++;\n" +
            "            switch (i) {\n" +
            "\n" +
            "                case 1:\n" +
            "                    System.out.println(i);\n" +
            "                    break;\n" +
            "                case 2:\n" +
            "                    System.out.println(\"retry\");\n" +
            "                    continue retry;\n" +
            "                case 3:\n" +
            "                    System.out.println(3);\n" +
            "                    break;\n" +
            "                default:\n" +
            "                    System.out.println(\"default\");\n" +
            "                    break;\n" +
            "\n" +
            "            }\n" +
            "\n" +
            "\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "\n" +
            "    public void method() {\n" +
            "        System.out.println(super.getClass().getName());\n" +
            "        System.out.println(this.getClass().getSuperclass().getName());\n" +
            "\n" +
            "    }\n" +
            "}\n" +
            "\n";

    public final DiagnosticCollector<JavaFileObject> diagnosticCollector;

    public final GlobalFilterClassLoader classLoader;

    public final GlobalFilterJavaFileManager javaFileManager;

    public final ByteArrayWrapper isolateNamespace;

    public CaJavaCompiler(byte[] isolateNamespace) {
        this.isolateNamespace = new ByteArrayWrapper(ByteUtil.copyFrom(isolateNamespace));
        diagnosticCollector = new DiagnosticCollector<>();

        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);

        classLoader = AccessController.doPrivileged(new PrivilegedAction<GlobalFilterClassLoader>() {
            @Override
            public GlobalFilterClassLoader run() {
                return new GlobalFilterClassLoader(parentLoader);
            }
        });
        javaFileManager = new GlobalFilterJavaFileManager(manager, classLoader);
    }

    public boolean compile(List<JavaSourceCodeEntity> javaSourceCodeEntities) {


        try {
            List<GlobalFilterJavaFileObject> javaFileObjects = new ArrayList<>(javaSourceCodeEntities.size());

            for (JavaSourceCodeEntity codeEntity: javaSourceCodeEntities) {

                String name = codeEntity.getClazzName();
                String sourceCode = codeEntity.getSourceCode();
                int i = name.lastIndexOf('.');
                String packageName = i < 0 ? "" : name.substring(0, i);
                String className = i < 0 ? name : name.substring(i + 1);
                GlobalFilterJavaFileObject javaFileObject = new GlobalFilterJavaFileObject(className, sourceCode);
                javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName,
                        className + JAVA_EXTENSION, javaFileObject);
                javaFileObjects.add(javaFileObject);
            }

            Boolean success = compiler.getTask(null, javaFileManager, diagnosticCollector, options,
                    null, javaFileObjects).call();
            if (success == null || !success) {


                StringBuilder errSb = new StringBuilder();
                for (Diagnostic diagnostic: diagnosticCollector.getDiagnostics()) {
                    errSb.append("error class [").
                            append(diagnostic.getSource()).
                            append("] , detail:").append(diagnostic.getMessage(Locale.ENGLISH)).append(".");
                }

                System.out.println("Compilation failed, detail: " + errSb.toString());
                return false;
            }


        } catch (Throwable t) {
            StringBuilder errSb = new StringBuilder();
            for (Diagnostic diagnostic: diagnosticCollector.getDiagnostics()) {
                errSb.append("error class [").
                        append(diagnostic.getSource()).
                        append("] , detail:").append(diagnostic.getMessage(Locale.ENGLISH)).append(".");
            }
            System.out.println("Compilation failed, detail: " + errSb.toString());
            return false;
        }
        return true;
    }



//
//    public Class<?> doCompile(String name, String sourceCode) throws Throwable {
//        int i = name.lastIndexOf('.');
//        String packageName = i < 0 ? "" : name.substring(0, i);
//        String className = i < 0 ? name : name.substring(i + 1);
//        GlobalFilterJavaFileObject javaFileObject = new GlobalFilterJavaFileObject(className, sourceCode);
//        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName,
//                className + JAVA_EXTENSION, javaFileObject);
//        Boolean result = resolver.getTask(null, javaFileManager, diagnosticCollector, options,
//                null, Arrays.asList(javaFileObject)).call();
//        if (result == null || !result) {
//            throw new IllegalStateException("Compilation failed. class: " + name + ", diagnostics: " + diagnosticCollector);
//        }
//        return classLoader.loadClass(name);
//    }

    public static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class GlobalFilterJavaFileObject extends SimpleJavaFileObject {

        private final CharSequence source;

        private ByteArrayOutputStream bytecode;

        public GlobalFilterJavaFileObject(final String baseName, final CharSequence source) {
            super(toURI(baseName + JAVA_EXTENSION), Kind.SOURCE);
            this.source = source;
        }

        GlobalFilterJavaFileObject(final String name, final Kind kind) {
            super(toURI(name), kind);
            source = null;
        }

        public GlobalFilterJavaFileObject(URI uri, Kind kind) {
            super(uri, kind);
            source = null;
        }

        @Override
        public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
            if (source == null) {
                throw new UnsupportedOperationException("source == null");
            }
            return source;
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(getByteCode());
        }

        @Override
        public OutputStream openOutputStream() {
            return bytecode = new ByteArrayOutputStream();
        }

        public byte[] getByteCode() {
            return bytecode.toByteArray();
        }
    }

    private static final class GlobalFilterJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        private final GlobalFilterClassLoader classLoader;

        private final Map<URI, JavaFileObject> fileObjects = new ConcurrentHashMap<URI, JavaFileObject>();

        public GlobalFilterJavaFileManager(JavaFileManager fileManager, GlobalFilterClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }

        @Override
        public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
            FileObject o = fileObjects.get(uri(location, packageName, relativeName));
            if (o != null) {
                return o;
            }
            return super.getFileForInput(location, packageName, relativeName);
        }

        public void putFileForInput(StandardLocation location, String packageName, String relativeName, JavaFileObject file) {
            fileObjects.put(uri(location, packageName, relativeName), file);
        }

        private URI uri(Location location, String packageName, String relativeName) {
            return toURI(location.getName() + '/' + packageName + '/' + relativeName);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, JavaFileObject.Kind kind, FileObject outputFile)
                throws IOException {
            JavaFileObject file = new GlobalFilterJavaFileObject(qualifiedName, kind);
            classLoader.add(qualifiedName, file);
            return file;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }

        @Override
        public String inferBinaryName(Location loc, JavaFileObject file) {
            if (file instanceof GlobalFilterJavaFileObject) {
                return file.getName();
            }
            return super.inferBinaryName(loc, file);
        }

    }

    private static final class GlobalFilterClassLoader extends ClassLoader {

        private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

        GlobalFilterClassLoader(final ClassLoader parentClassLoader) {
            super(parentClassLoader);
        }

        @Override
        protected Class<?> findClass(final String qualifiedClassName) throws ClassNotFoundException {
            JavaFileObject file = classes.get(qualifiedClassName);
            if (file != null) {


                byte[] bytes = ((GlobalFilterJavaFileObject) file).getByteCode();
                return defineClass(qualifiedClassName, bytes, 0, bytes.length);
            }
            try {
                return getClass().getClassLoader().loadClass(qualifiedClassName);
            } catch (ClassNotFoundException nf) {
                return super.findClass(qualifiedClassName);
            }
        }

        void add(final String qualifiedClassName, final JavaFileObject javaFile) {
            classes.put(qualifiedClassName, javaFile);
        }

        @Override
        protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }

    public static void main(String[] args) {

        test();
    }

    private static void test() {
        {
            try {
                byte[] namesapceBytes = HashUtil.randomHash();
                CaJavaCompiler caJavaCompiler = new CaJavaCompiler(namesapceBytes);
                List<JavaSourceCodeEntity> javaCodeSourceEntities1 = Arrays.asList(
                        new JavaSourceCodeEntity(clazzName, code));

                boolean result1 = caJavaCompiler.compile(javaCodeSourceEntities1);
                System.out.println(result1);


                Class testClazz = Class.forName(clazzName, false, caJavaCompiler.classLoader);
                for (Method method : testClazz.getDeclaredMethods()) {
                    System.out.println(method.getName());
                }

                System.out.println();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        System.out.println("=================================");
    }



}
