package com.practice.simpehttpserver.server;

import java.io.*;
        import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author: Linda
 * @date: 2026/3/4 14:01
 * @description:
 */

public class RequestHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private final Socket clientSocket;
    private final String webRoot;

    // HTTP状态码
    private static final int OK = 200;
    private static final int NOT_FOUND = 404;
    private static final int INTERNAL_SERVER_ERROR = 500;

    // 响应头信息
    private static final Map<Integer, String> STATUS_MESSAGES = new HashMap<>();
    static {
        STATUS_MESSAGES.put(OK, "OK");
        STATUS_MESSAGES.put(NOT_FOUND, "Not Found");
        STATUS_MESSAGES.put(INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    public RequestHandler(Socket clientSocket, String webRoot) {
        this.clientSocket = clientSocket;
        this.webRoot = webRoot;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // 解析请求行
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            logger.info("收到请求: " + requestLine);

            // 解析请求
            Request request = parseRequest(requestLine, in);

            // 处理请求
            if ("GET".equals(request.method)) {
                handleGet(request, out);
            } else if ("POST".equals(request.method)) {
                handlePost(request, out);
            } else {
                sendError(out, INTERNAL_SERVER_ERROR, "不支持的HTTP方法: " + request.method);
            }

        } catch (IOException e) {
            logger.severe("处理请求失败: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.severe("关闭客户端连接失败: " + e.getMessage());
            }
        }
    }

    private Request parseRequest(String requestLine, BufferedReader in) throws IOException {
        Request request = new Request();

        // 解析请求行: "GET /index.html HTTP/1.1"
        String[] parts = requestLine.split(" ");
        if (parts.length >= 2) {
            request.method = parts[0];
            String path = parts[1];

            // 分离路径和查询参数
            int queryIndex = path.indexOf('?');
            if (queryIndex != -1) {
                request.path = path.substring(0, queryIndex);
                request.queryString = path.substring(queryIndex + 1);
                parseQueryString(request.queryString, request.parameters);
            } else {
                request.path = path;
            }
        }

        // 解析请求头
        String line;
        int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                request.headers.put(key, value);

                if ("Content-Length".equalsIgnoreCase(key)) {
                    contentLength = Integer.parseInt(value);
                }
            }
        }

        // 读取请求体（如果是POST请求）
        if ("POST".equals(request.method) && contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            request.body = new String(bodyChars);
            parseQueryString(request.body, request.parameters);
        }

        return request;
    }

    private void parseQueryString(String queryString, Map<String, String> parameters) {
        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    parameters.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    logger.warning("解析参数失败: " + pair);
                }
            }
        }
    }

    private void handleGet(Request request, OutputStream out) throws IOException {
        String filePath = request.path;

        // 默认返回index.html
        if ("/".equals(filePath)) {
            filePath = "/index.html";
        }

        Path path = Paths.get(webRoot, filePath);
        File file = path.toFile();

        if (file.exists() && !file.isDirectory()) {
            byte[] content = Files.readAllBytes(path);
            String contentType = getContentType(filePath);
            sendResponse(out, OK, contentType, content);
        } else {
            // 返回404页面
            sendError(out, NOT_FOUND, "文件未找到: " + filePath);
        }
    }

    private void handlePost(Request request, OutputStream out) throws IOException {
        // 简单的POST处理，返回接收到的参数
        StringBuilder response = new StringBuilder();
        response.append("<html><body>");
        response.append("<h1>POST请求已接收</h1>");
        response.append("<h2>请求参数:</h2>");
        response.append("<ul>");

        for (Map.Entry<String, String> param : request.parameters.entrySet()) {
            response.append("<li><strong>")
                    .append(param.getKey())
                    .append(":</strong> ")
                    .append(param.getValue())
                    .append("</li>");
        }

        response.append("</ul>");
        response.append("</body></html>");

        sendResponse(out, OK, "text/html", response.toString().getBytes());
    }

    private void sendError(OutputStream out, int statusCode, String message) throws IOException {
        String errorPagePath = "/" + statusCode + ".html";
        Path path = Paths.get(webRoot, errorPagePath);
        File file = path.toFile();

        byte[] content;
        if (file.exists()) {
            content = Files.readAllBytes(path);
        } else {
            // 如果没有自定义错误页面，生成简单的错误信息
            String errorContent = String.format(
                    "<html><body><h1>%d %s</h1><p>%s</p></body></html>",
                    statusCode, STATUS_MESSAGES.get(statusCode), message
            );
            content = errorContent.getBytes();
        }

        sendResponse(out, statusCode, "text/html", content);
    }

    private void sendResponse(OutputStream out, int statusCode, String contentType, byte[] content) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        // 发送状态行
        writer.printf("HTTP/1.1 %d %s\r\n", statusCode, STATUS_MESSAGES.get(statusCode));

        // 发送响应头
        writer.printf("Content-Type: %s\r\n", contentType);
        writer.printf("Content-Length: %d\r\n", content.length);
        writer.printf("Connection: close\r\n");
        writer.printf("\r\n");
        writer.flush();

        // 发送响应体
        out.write(content);
        out.flush();

        logger.info(String.format("响应: %d %s (%d 字节)",
                statusCode, STATUS_MESSAGES.get(statusCode), content.length));
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else if (filePath.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

    // 内部类：表示HTTP请求
    private static class Request {
        String method;
        String path;
        String queryString;
        String body;
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
    }
}