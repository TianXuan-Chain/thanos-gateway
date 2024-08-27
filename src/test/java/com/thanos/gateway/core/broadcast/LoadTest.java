package com.thanos.gateway.core.broadcast;

import javax.tools.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * LoadTest.java description：
 *
 * @Author laiyiyu create on 2021-03-24 14:34:46
 */
public class LoadTest {


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

    public static void main(String[] args) {
        Class<?> clazz = compile(clazzName, code);

        try {
            // 生成对象
            Object obj = clazz.newInstance();
            Class<? extends Object> cls = obj.getClass();
            // 调用main方法
            Method m = clazz.getMethod("main", String[].class);
            Object invoke = m.invoke(obj, new Object[] { new String[] {} });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 装载字符串成为java可执行文件
     * @param className className
     * @param javaCodes javaCodes
     * @return Class
     */
    private  static Class<?> compile(String className, String javaCodes) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        StrSrcJavaObject srcObject = new StrSrcJavaObject(className, javaCodes);
        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(srcObject);
        String flag = "-d";
        String outDir = "";
        try {
            File classPath = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            outDir = classPath.getAbsolutePath() + File.separator;
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        Iterable<String> options = Arrays.asList(flag, outDir);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        boolean result = task.call();
        if (result == true) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    private static class StrSrcJavaObject extends SimpleJavaFileObject {

        private String content;

        StrSrcJavaObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }
}
