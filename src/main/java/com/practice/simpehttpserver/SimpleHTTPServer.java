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


}