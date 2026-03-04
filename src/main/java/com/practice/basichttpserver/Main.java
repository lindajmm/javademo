package com.practice.simpehttpserver;
import server.SimpleHttpServer;
import client.HttpClient;

/**
 * @author: Linda
 * @date: 2026/3/4 14:00
 * @description:
 */




public class Main2 {
    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();

        switch (mode) {
            case "server":
                startServer(args);
                break;
            case "client":
                startClient(args);
                break;
            default:
                printUsage();
        }
    }

    private static void startServer(String[] args) {
        int port = 8080; // 默认端口
        String webRoot = "./webroot"; // 默认静态文件目录

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("无效的端口号，使用默认端口 8080");
            }
        }

        if (args.length >= 3) {
            webRoot = args[2];
        }

        SimpleHttpServer server = new SimpleHttpServer(port, webRoot);
        server.start();
    }

    private static void startClient(String[] args) {
        if (args.length < 3) {
            System.out.println("客户端用法: java Main client <URL> <method> [data]");
            System.out.println("示例: java Main client http://localhost:8080/index.html GET");
            System.out.println("      java Main client http://localhost:8080/test POST 'name=John&age=25'");
            return;
        }

        String url = args[1];
        String method = args[2].toUpperCase();
        String data = args.length >= 4 ? args[3] : null;

        HttpClient client = new HttpClient();
        try {
            if ("GET".equals(method)) {
                client.sendGet(url);
            } else if ("POST".equals(method)) {
                client.sendPost(url, data);
            } else {
                System.out.println("不支持的HTTP方法: " + method);
            }
        } catch (Exception e) {
            System.err.println("客户端请求失败: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("简易HTTP服务器使用说明:");
        System.out.println("  启动服务器: java Main server [port] [webroot]");
        System.out.println("  示例: java Main server 8080 ./webroot");
        System.out.println("  启动客户端: java Main client <URL> <method> [data]");
        System.out.println("  示例: java Main client http://localhost:8080/index.html GET");
        System.out.println("        java Main client http://localhost:8080/test POST 'name=John'");
    }
}
