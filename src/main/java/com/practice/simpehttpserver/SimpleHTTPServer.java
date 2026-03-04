package com.practice.simpehttpserver;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author: Linda
 * @date: 2026/1/29 11:28
 * @description:
 */

public class SimpleHTTPServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private String rootDirectory;
    private int port;
    private boolean isRunning;

    // 支持的MIME类型
    static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("pdf", "application/pdf");
    }

    /**
     * 构造函数
     * @param port 服务器端口
     * @param rootDirectory 静态文件根目录
     * @param maxThreads 最大线程数
     */
    public SimpleHTTPServer(int port, String rootDirectory, int maxThreads) {
        this.port = port;
        this.rootDirectory = rootDirectory;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);

        // 如果根目录不存在，则创建
        File rootDir = new File(rootDirectory);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
            createSampleFiles(rootDir);
        }
    }

    /**
     * 启动服务器
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("HTTP服务器启动在端口 " + port);
            System.out.println("静态文件根目录: " + new File(rootDirectory).getAbsolutePath());
            System.out.println("访问地址: http://localhost:" + port);

            // 主循环，接受客户端连接
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        }
    }

    /**
     * 停止服务器
     */
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("停止服务器时出错: " + e.getMessage());
        }
        threadPool.shutdown();
        System.out.println("服务器已停止");
    }






    /**
     * 创建示例文件
     */
    private void createSampleFiles(File rootDir) {
        // 定义需要创建的文件列表
        List<String> templates = Arrays.asList(
                "index.html",
                "about.html",
                "style.css"
        );

        for (String template : templates) {
            createFileFromResource(rootDir, template);
        }
    }

    private void createFileFromResource(File rootDir, String filename) {
        try {
            // 从类路径读取模板文件
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/" + filename);

            if (inputStream == null) {
                // 如果没有找到资源文件，使用内置默认内容
                createDefaultFile(rootDir, filename);
                return;
            }

            // 将资源文件复制到目标目录
            Path targetPath = Paths.get(rootDir.getAbsolutePath(), filename);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("已创建文件: " + filename);

        } catch (IOException e) {
            System.err.println("创建文件 " + filename + " 时出错: " + e.getMessage());
        }
    }

    // 默认内容作为备选
    private void createDefaultFile(File rootDir, String filename) throws IOException {
        File targetFile = new File(rootDir, filename);

        switch (filename) {
            case "index.html":
                try (FileWriter writer = new FileWriter(targetFile)) {
                    writer.write(getDefaultIndexHtml());
                }
                break;
            case "about.html":
                try (FileWriter writer = new FileWriter(targetFile)) {
                    writer.write(getDefaultAboutHtml());
                }
                break;
            case "style.css":
                try (FileWriter writer = new FileWriter(targetFile)) {
                    writer.write(getDefaultStyleCss());
                }
                break;
        }
        System.out.println("已使用默认内容创建文件: " + filename);
    }

    // 简化的默认内容方法（只包含关键部分）
    private String getDefaultIndexHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("    <title>Simple HTTP Server</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h1>Welcome to use Simple HTTP Server</h1>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }
    // 简化的默认内容方法（只包含关键部分）
    private String getDefaultAboutHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("    <title>Simple HTTP Server</title>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h1>About Welcome to use Simple HTTP Server</h1>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        // ... 简化的HTML内容
        return sb.toString();
    }

    // 简化的默认内容方法（只包含关键部分）
    private String getDefaultStyleCss() {
        StringBuilder sb = new StringBuilder();
        sb.append("body {\n");
        sb.append("    font-family: Arial, sans-serif;\n");
        sb.append("    line-height: 1.6;\n");
        sb.append(" }\n");
        return sb.toString();
    }

// 其他默认内容方法类似...
  /*  private void createSampleFiles(File rootDir) {
        try {
            // 创建示例HTML文件
            File indexFile = new File(rootDir, "index.html");
            FileWriter writer = new FileWriter(indexFile);
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("    <title>简易HTTP服务器</title>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
            writer.write("        h1 { color: #333; }\n");
            writer.write("        .container { max-width: 800px; margin: 0 auto; }\n");
            writer.write("        .form-group { margin: 20px 0; }\n");
            writer.write("        label { display: block; margin-bottom: 5px; }\n");
            writer.write("        input, textarea { width: 100%; padding: 8px; }\n");
            writer.write("        button { padding: 10px 20px; background-color: #4CAF50; color: white; border: none; cursor: pointer; }\n");
            writer.write("        .response { margin-top: 20px; padding: 10px; background-color: #f0f0f0; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <div class=\"container\">\n");
            writer.write("        <h1>欢迎使用简易HTTP服务器</h1>\n");
            writer.write("        <p>这是一个使用Java实现的简易HTTP服务器示例。</p>\n");
            writer.write("        \n");
            writer.write("        <h2>GET请求示例</h2>\n");
            writer.write("        <p>访问以下链接测试GET请求:</p>\n");
            writer.write("        <ul>\n");
            writer.write("            <li><a href=\"/test-get?name=张三&age=25\">GET请求示例</a></li>\n");
            writer.write("            <li><a href=\"/about.html\">关于页面</a></li>\n");
            writer.write("        </ul>\n");
            writer.write("        \n");
            writer.write("        <h2>POST请求示例</h2>\n");
            writer.write("        <form id=\"post-form\" method=\"post\">\n");
            writer.write("            <div class=\"form-group\">\n");
            writer.write("                <label for=\"username\">用户名:</label>\n");
            writer.write("                <input type=\"text\" id=\"username\" name=\"username\" value=\"测试用户\">\n");
            writer.write("            </div>\n");
            writer.write("            <div class=\"form-group\">\n");
            writer.write("                <label for=\"message\">消息:</label>\n");
            writer.write("                <textarea id=\"message\" name=\"message\" rows=\"4\">这是一个测试消息</textarea>\n");
            writer.write("            </div>\n");
            writer.write("            <button type=\"submit\">提交POST请求</button>\n");
            writer.write("        </form>\n");
            writer.write("        <div id=\"response\" class=\"response\"></div>\n");
            writer.write("        \n");
            writer.write("        <h2>错误页面测试</h2>\n");
            writer.write("        <ul>\n");
            writer.write("            <li><a href=\"/notfound.html\">404页面测试</a></li>\n");
            writer.write("            <li><a href=\"/error-test\">500错误测试</a></li>\n");
            writer.write("        </ul>\n");
            writer.write("    </div>\n");
            writer.write("    \n");
            writer.write("    <script>\n");
            writer.write("        document.getElementById('post-form').addEventListener('submit', async function(e) {\n");
            writer.write("            e.preventDefault();\n");
            writer.write("            \n");
            writer.write("            const formData = new FormData(this);\n");
            writer.write("            const params = new URLSearchParams(formData).toString();\n");
            writer.write("            \n");
            writer.write("            try {\n");
            writer.write("                const response = await fetch('/test-post', {\n");
            writer.write("                    method: 'POST',\n");
            writer.write("                    headers: {\n");
            writer.write("                        'Content-Type': 'application/x-www-form-urlencoded'\n");
            writer.write("                    },\n");
            writer.write("                    body: params\n");
            writer.write("                });\n");
            writer.write("                \n");
            writer.write("                const text = await response.text();\n");
            writer.write("                document.getElementById('response').innerHTML = '<h3>服务器响应:</h3><pre>' + text + '</pre>';\n");
            writer.write("            } catch (error) {\n");
            writer.write("                document.getElementById('response').innerHTML = '<h3>请求失败:</h3><pre>' + error + '</pre>';\n");
            writer.write("            }\n");
            writer.write("        });\n");
            writer.write("    </script>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.close();

            // 创建示例关于页面
            File aboutFile = new File(rootDir, "about.html");
            writer = new FileWriter(aboutFile);
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("    <title>关于</title>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
            writer.write("        h1 { color: #333; }\n");
            writer.write("        .container { max-width: 800px; margin: 0 auto; }\n");
            writer.write("        .back-link { margin-top: 20px; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <div class=\"container\">\n");
            writer.write("        <h1>关于这个服务器</h1>\n");
            writer.write("        <p>这是一个使用Java实现的简易HTTP服务器。</p>\n");
            writer.write("        <p>功能包括：</p>\n");
            writer.write("        <ul>\n");
            writer.write("            <li>处理GET和POST请求</li>\n");
            writer.write("            <li>返回静态文件</li>\n");
            writer.write("            <li>支持404/500错误页面</li>\n");
            writer.write("            <li>多线程处理客户端请求</li>\n");
            writer.write("        </ul>\n");
            writer.write("        <div class=\"back-link\">\n");
            writer.write("            <a href=\"/index.html\">返回首页</a>\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
            writer.close();

            // 创建示例CSS文件
            File cssFile = new File(rootDir, "style.css");
            writer = new FileWriter(cssFile);
            writer.write("body {\n");
            writer.write("    font-family: Arial, sans-serif;\n");
            writer.write("    line-height: 1.6;\n");
            writer.write("    margin: 0;\n");
            writer.write("    padding: 20px;\n");
            writer.write("    background-color: #f5f5f5;\n");
            writer.write("}\n");
            writer.write("\n");
            writer.write(".container {\n");
            writer.write("    max-width: 800px;\n");
            writer.write("    margin: 0 auto;\n");
            writer.write("    background-color: white;\n");
            writer.write("    padding: 20px;\n");
            writer.write("    border-radius: 5px;\n");
            writer.write("    box-shadow: 0 2px 5px rgba(0,0,0,0.1);\n");
            writer.write("}\n");
            writer.write("\n");
            writer.write("h1 {\n");
            writer.write("    color: #333;\n");
            writer.write("    border-bottom: 2px solid #4CAF50;\n");
            writer.write("    padding-bottom: 10px;\n");
            writer.write("}\n");
            writer.close();

        } catch (IOException e) {
            System.err.println("创建示例文件时出错: " + e.getMessage());
        }
    }*/

    class ClientHandler implements Runnable {
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

    public static void main(String[] args) {
        int port = 8081;
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