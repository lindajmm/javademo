package com.practice.simpehttpserver;


import java.nio.charset.StandardCharsets;

/**
 * @author: Linda
 * @date: 2026/1/29 16:30
 * @description:
 */
public class Main {
    public static void main(String[] args) {
        // 测试示例
        String jsonString = "{\n" +
                "  \"username\": \"zhangsan\",\n" +
                "  \"email\": \"zhangsan@example.com\",\n" +
                "  \"password\": \"SecurePass123!\",\n" +
                "  \"phone\": \"13800138000\"\n" +
                "}";

        System.out.println("=== JSON长度计算 ===");
        System.out.println("JSON字符串:\n" + jsonString);
        System.out.println("\n字符数: " + jsonString.length());
        byte[] utf8Bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        int byteCount = utf8Bytes.length;
        System.out.println("字节数（UTF-8）: " + byteCount);

//        // 不同编码的长度
//        System.out.println("\n不同编码的字节长度:");
//        System.out.println("UTF-8: " + calculateContentLength(jsonString, StandardCharsets.UTF_8));
//        System.out.println("UTF-16: " + calculateContentLength(jsonString, StandardCharsets.UTF_16));
//        System.out.println("ISO-8859-1: " + calculateContentLength(jsonString, StandardCharsets.ISO_8859_1));
    }

    public /*static*/ void main11(String[] args) {
        int port = 8080;
        String rootDir = "webroot";
        int maxThreads = 10;

        // 解析命令行参数
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                case "--port":
                    if (i + 1 < args.length) {
                        port = Integer.parseInt(args[++i]);
                    }
                    break;
                case "-d":
                case "--dir":
                    if (i + 1 < args.length) {
                        rootDir = args[++i];
                    }
                    break;
                case "-t":
                case "--threads":
                    if (i + 1 < args.length) {
                        maxThreads = Integer.parseInt(args[++i]);
                    }
                    break;
                case "-h":
                case "--help":
                    System.out.println("用法: java SimpleHttpServer [选项]");
                    System.out.println("选项:");
                    System.out.println("  -p, --port <端口>     服务器端口 (默认: 8080)");
                    System.out.println("  -d, --dir <目录>      静态文件根目录 (默认: webroot)");
                    System.out.println("  -t, --threads <数量>  最大线程数 (默认: 10)");
                    System.out.println("  -h, --help           显示此帮助信息");
                    return;
            }
        }

        // 创建并启动服务器
        SimpleHTTPServer server = new SimpleHTTPServer(port, rootDir, maxThreads);

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在关闭服务器...");
            server.stop();
        }));

        // 启动服务器
        server.start();
    }

}
