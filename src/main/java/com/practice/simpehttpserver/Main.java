package com.practice.simpehttpserver;


/**
 * @author: Linda
 * @date: 2026/1/29 16:30
 * @description:
 */
public class Main {

    public static void main(String[] args) {
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
