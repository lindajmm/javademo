package com.practice.basichttpserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

/**
 * @author: Linda
 * @date: 2026/3/4 14:02
 * @description:
 */
public class HttpClient {

    public void sendGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        String host = url.getHost();
        int port = url.getPort() != -1 ? url.getPort() : 80;
        String path = url.getPath();
        String query = url.getQuery();

        if (path == null || path.isEmpty()) {
            path = "/";
        }

        if (query != null && !query.isEmpty()) {
            path += "?" + query;
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 发送GET请求
            out.println("GET " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("User-Agent: SimpleJavaHttpClient");
            out.println("Accept: */*");
            out.println("Connection: close");
            out.println();

            // 读取并打印响应
            printResponse(in);
        }
    }

    public void sendPost(String urlString, String data) throws Exception {
        URL url = new URL(urlString);
        String host = url.getHost();
        int port = url.getPort() != -1 ? url.getPort() : 80;
        String path = url.getPath();

        if (path == null || path.isEmpty()) {
            path = "/";
        }

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 发送POST请求
            out.println("POST " + path + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("User-Agent: SimpleJavaHttpClient");
            out.println("Accept: */*");
            out.println("Content-Type: application/x-www-form-urlencoded");
            out.println("Content-Length: " + (data != null ? data.length() : 0));
            out.println("Connection: close");
            out.println();

            if (data != null && !data.isEmpty()) {
                out.println(data);
            }

            // 读取并打印响应
            printResponse(in);
        }
    }

    private void printResponse(BufferedReader in) throws IOException {
        System.out.println("\n=== 服务器响应 ===");

        String line;
        boolean isHeader = true;
        StringBuilder body = new StringBuilder();

        while ((line = in.readLine()) != null) {
            if (isHeader) {
                if (line.isEmpty()) {
                    isHeader = false;
                    System.out.println(); // 空行分隔头和体
                } else {
                    System.out.println("[Header] " + line);
                }
            } else {
                body.append(line).append("\n");
            }
        }

        System.out.println("\n[Body]\n" + body.toString());
    }
}
