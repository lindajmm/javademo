package com.practice.simpehttpserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.practice.simpehttpserver.SimpleHTTPServer.MIME_TYPES;

/**
 * @author: Linda
 * @date: 2026/1/29 11:36
 * @description:
 */

/**
 * 客户端请求处理器
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String rootDirectory = "webroot";

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            // 读取请求的第一行
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("收到请求: " + requestLine);

            // 解析请求行
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 3) {
                sendErrorResponse(out, 400, "Bad Request");
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];
            String httpVersion = requestParts[2];

            // 读取请求头
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                int separator = headerLine.indexOf(":");
                if (separator > 0) {
                    String key = headerLine.substring(0, separator).trim();
                    String value = headerLine.substring(separator + 1).trim();
                    headers.put(key.toLowerCase(), value);
                }
            }

            // 读取请求体（对于POST请求）
            String requestBody = null;
            if ("POST".equalsIgnoreCase(method)) {
                int contentLength = 0;
                if (headers.containsKey("content-length")) {
                    contentLength = Integer.parseInt(headers.get("content-length"));
                }

                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars, 0, contentLength);
                    requestBody = new String(bodyChars);
                }
            }

            // 处理请求
            handleRequest(method, path, headers, requestBody, out);

        } catch (Exception e) {
            System.err.println("处理客户端请求时出错: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("关闭客户端连接时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 处理HTTP请求
     */
    private void handleRequest(String method, String path,
                               Map<String, String> headers,
                               String requestBody,
                               OutputStream out) throws IOException {

        // 处理特殊路径
        if ("/test-get".equals(path)) {
            handleTestGet(out);
            return;
        }

        if ("/test-post".equals(path) && "POST".equalsIgnoreCase(method)) {
            handleTestPost(requestBody, out);
            return;
        }

        if ("/error-test".equals(path)) {
            handleErrorTest(out);
            return;
        }

        // 处理静态文件请求
        if ("GET".equalsIgnoreCase(method)) {
            serveStaticFile(path, out);
        } else {
            sendErrorResponse(out, 405, "Method Not Allowed");
        }
    }

    /**
     * 处理GET请求测试
     */
    private void handleTestGet(OutputStream out) throws IOException {
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>GET请求测试</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; }\n" +
                "        .container { max-width: 800px; margin: 0 auto; }\n" +
                "        .back-link { margin-top: 20px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>GET请求测试成功</h1>\n" +
                "        <p>这是一个GET请求的测试响应。</p>\n" +
                "        <p>当前时间: " + new Date() + "</p>\n" +
                "        <p>服务器信息: Java简易HTTP服务器 v1.0</p>\n" +
                "        <div class=\"back-link\">\n" +
                "            <a href=\"/index.html\">返回首页</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        sendResponse(out, 200, "OK", "text/html", html);
    }

    /**
     * 处理POST请求测试
     */
    private void handleTestPost(String requestBody, OutputStream out) throws IOException {
        String responseText;

        if (requestBody != null && !requestBody.isEmpty()) {
            // 解析POST数据
            Map<String, String> params = parseFormData(requestBody);
            responseText = "POST请求处理成功！\n\n";
            responseText += "接收到的数据:\n";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                responseText += entry.getKey() + ": " + entry.getValue() + "\n";
            }
            responseText += "\n处理时间: " + new Date();
        } else {
            responseText = "POST请求处理成功，但未收到数据。\n\n";
            responseText += "处理时间: " + new Date();
        }

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>POST请求测试</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; }\n" +
                "        .container { max-width: 800px; margin: 0 auto; }\n" +
                "        .back-link { margin-top: 20px; }\n" +
                "        pre { background-color: #f5f5f5; padding: 10px; border-radius: 5px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>POST请求测试成功</h1>\n" +
                "        <pre>" + responseText + "</pre>\n" +
                "        <div class=\"back-link\">\n" +
                "            <a href=\"/index.html\">返回首页</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        sendResponse(out, 200, "OK", "text/html", html);
    }

    /**
     * 解析表单数据
     */
    private Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), java.nio.charset.StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), java.nio.charset.StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * 处理错误测试
     */
    private void handleErrorTest(OutputStream out) throws IOException {
        // 模拟服务器内部错误
        sendErrorResponse(out, 500, "Internal Server Error (测试)");
    }

    /**
     * 提供静态文件服务
     */
    private void serveStaticFile(String path, OutputStream out) throws IOException {
        // 将URL路径转换为文件系统路径
        String filePath = path;
        if ("/".equals(path)) {
            filePath = "/index.html";
        }

        File file = new File(rootDirectory + filePath);

        // 检查文件是否存在且可读
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            sendErrorResponse(out, 404, "File Not Found");
            return;
        }

        // 检查文件是否在根目录内（防止路径遍历攻击）
        if (!file.getCanonicalPath().startsWith(new File(rootDirectory).getCanonicalPath())) {
            sendErrorResponse(out, 403, "Forbidden");
            return;
        }

        // 确定MIME类型
        String mimeType = "application/octet-stream";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        }

        // 读取文件内容
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // 发送响应
        sendResponse(out, 200, "OK", mimeType, fileContent);
    }

    /**
     * 发送HTTP响应
     */
    private void sendResponse(OutputStream out, int statusCode, String statusMessage,
                              String contentType, String content) throws IOException {
        sendResponse(out, statusCode, statusMessage, contentType, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private void sendResponse(OutputStream out, int statusCode, String statusMessage,
                              String contentType, byte[] content) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8));

        // 发送状态行
        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);

        // 发送响应头
        writer.println("Content-Type: " + contentType + "; charset=utf-8");
        writer.println("Content-Length: " + content.length);
        writer.println("Connection: close");
        writer.println("Server: SimpleHttpServer/1.0");
        writer.println(); // 空行分隔头部和主体

        writer.flush(); // 确保头部已经发送

        // 发送响应主体
        out.write(content);
        out.flush();
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(OutputStream out, int statusCode, String statusMessage) throws IOException {
        String title, description;

        switch (statusCode) {
            case 400:
                title = "400 Bad Request";
                description = "服务器无法理解请求的语法。";
                break;
            case 403:
                title = "403 Forbidden";
                description = "服务器拒绝请求。";
                break;
            case 404:
                title = "404 Not Found";
                description = "服务器找不到请求的资源。";
                break;
            case 405:
                title = "405 Method Not Allowed";
                description = "请求方法不被允许。";
                break;
            case 500:
                title = "500 Internal Server Error";
                description = "服务器遇到错误，无法完成请求。";
                break;
            default:
                title = statusCode + " Error";
                description = "发生错误。";
        }

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>" + title + "</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; text-align: center; }\n" +
                "        .error-container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                "        h1 { color: #d32f2f; font-size: 3em; margin-bottom: 10px; }\n" +
                "        p { font-size: 1.2em; margin-bottom: 20px; }\n" +
                "        .back-link { margin-top: 30px; }\n" +
                "        a { color: #1976d2; text-decoration: none; }\n" +
                "        a:hover { text-decoration: underline; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error-container\">\n" +
                "        <h1>" + title + "</h1>\n" +
                "        <p>" + description + "</p>\n" +
                "        <p>简易HTTP服务器</p>\n" +
                "        <div class=\"back-link\">\n" +
                "            <a href=\"/index.html\">返回首页</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        sendResponse(out, statusCode, statusMessage, "text/html", html);
    }
}
