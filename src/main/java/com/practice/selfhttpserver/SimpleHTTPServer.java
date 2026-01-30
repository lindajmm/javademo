package com.practice.selfhttpserver;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Linda
 * @date: 2026/1/29 12:13
 * @description:
 */
public class SimpleHTTPServer {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private String rootDirectory;
    private int port;
    private boolean isRunning;

    //支持的MIME类型
    static final Map<String, String> MIME_TYPE = new HashMap<>();

    static{
        MIME_TYPE.put("html", "text/html;");
        MIME_TYPE.put("htm", "text/htm;");
        MIME_TYPE.put("txt", "text/plain;");
        MIME_TYPE.put("css", "text/css;");
        MIME_TYPE.put("js", "application/javascript;");
        MIME_TYPE.put("json", "application/json;");
        MIME_TYPE.put("png", "image/png;");
        MIME_TYPE.put("jpg", "image/jpeg;");//>?
        MIME_TYPE.put("jpeg", "image/jpeg;");
        MIME_TYPE.put("gif", "image/gif;");
        MIME_TYPE.put("ico", "image/x-icon;");
        MIME_TYPE.put("pdf", "application/pdf;");
    }

    public SimpleHTTPServer(int port, String rootDirectory, int maxThreads){
        this.port = port;
        this.rootDirectory = rootDirectory;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);

        //如果目录不存在，则创建
        File rootDir = new File(rootDirectory);
        if(!rootDir.exists()){
            rootDir.mkdirs();
            createSampleFiles(rootDir);
        }
    }

    //启动服务
    public void start() {
        try{
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("HTTP 服务器启动在端口: "+port);
            System.out.println("静态文件根目录： "+ new File(rootDirectory).getAbsolutePath());
            System.out.println("访问地址： http://localhost:"+port);

            //主循环，接受客户端连接
            while(isRunning){
                Socket clientSocket = serverSocket.accept();
//                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败： "+ e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        isRunning = false;
        try{
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e) {
           System.err.println("没能成功停止服务器： "+ e.getMessage());
            throw new RuntimeException(e);
        }
        threadPool.shutdown();
        System.out.println("服务器已停止。");
    }

    /**
     创建示例文件
     */
    private void createSampleFiles(File rootDir){
        //定义需要创建的文件列表
        List<String> templates = Arrays.asList(
                "index.html",
                "about.html",
                "style.css"
        );

        for(String template : templates) {
            createFileFromResource(rootDir, template);
        }
    }

    private void createFileFromResource(File rootDir, String filename) {
        try {
            //从类路径读取模版文件
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("templates/" + filename);

            if (inputStream == null) {
                createDefaultFile(rootDir, filename);
                return;
            }

            //将资源文件复制到目标目录
            Path targetPath = Paths.get(rootDir.getAbsolutePath(), filename);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("已经创建文件： " + filename);
        } catch (IOException e) {
            System.err.println("创建文件 " + filename + "时出错： " + e.getMessage());
        }
    }

    private void createDefaultFile(File rootDir, String filename) throws IOException {
        File targetFile = new File(rootDir, filename);
        switch(filename){
            case "index.html":
                try(FileWriter writer = new FileWriter(targetFile)){
                    writer.write(getDefaultIndexHtml());
                }
                break;
            case "about.html":
                try(FileWriter writer = new FileWriter(targetFile)){
                    writer.write(getDefaultAboutHtml());
                }
                break;
            case "style.css":
                try(FileWriter writer = new FileWriter(targetFile)){
                    writer.write(getDefaultStyleCss());
                }
            default:
                System.out.println("filename is not supported!");
        }
        System.out.println("已使用默认内容创建文件： "+ filename);
    }

    private String getDefaultIndexHtml(){
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <title>Simple HTTP Serve</title>
                </head>
                <body>
                <h1>Welcome to use Simple HTTP Server</h1>
                </body>
                </html>
                """;
    }

    private String getDefaultAboutHtml(){
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <title>Simple HTTP Server</title>
                </head>
                <body>
                <h1>About Welcome to use Simple HTTP Server</h1>
                </body>
                </html>
                """;
    }

    private String getDefaultStyleCss(){
        return """
                body {
                      font-family: Arial, sans-serif;
                      line-height: 1.6;
                    }
                """;
    }
}
