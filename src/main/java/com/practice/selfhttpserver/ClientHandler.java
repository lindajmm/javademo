package com.practice.selfhttpserver;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.*;
import static com.practice.selfhttpserver.SimpleHTTPServer.MIME_TYPE;

/**
 * @author: Linda
 * @date: 2026/1/30 17:14
 * @description:
 */
public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private String rootDirectory = "webrootlinda";

    public ClientHandler(Socket socket){
        this.clientSocket = socket;
    }

    @Override
    public void run() {
//        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8), 8192);
            OutputStream out = clientSocket.getOutputStream()) {
            //读取请求的第一行
            String requestLine = in.readLine();
            if(requestLine == null ||requestLine.isEmpty()){
                return;
            }
            System.out.println("收到请求： "+ requestLine);

            //解析请求行
            String[] requestParts = requestLine.split(" ");
            if(requestParts.length < 3){
                sendErrorResponse(out, 400, "Bad request");
                return;
            }

            String method = requestParts[0];
            String path = requestParts[1];
            String httpVersion = requestParts[2];

            //读取请求头
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while((headerLine = in.readLine()) != null && !headerLine.isEmpty()){
                int separator = headerLine.indexOf(":");
                if(separator > 0){
                    headers.put(headerLine.substring(0,separator).trim(), headerLine.substring(separator+1).trim());
                }
            }

            //读取请求体（对于POST请求）
            String requestBody = null;
            if("POST".equalsIgnoreCase(method)){
                int contentLength =0;
                if(headers.containsKey("Content-Length")){
                    contentLength = Integer.parseInt(headers.get("Content-Length"));
                }
                System.out.println("请求体的长度是 "+ contentLength);

                if(contentLength >0){
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars, 0, contentLength);
                    // Q:如何保证in.read(bodyChars, 0, contentLength)能读到request body??
                    //A: while((headerLine = in.readLine()) != null && !headerLine.isEmpty()){
                    //这一行已经把请求头和空行读完了，后面紧接着就是请求体
                    requestBody = new String(bodyChars);
                    System.out.println("request body is: " + requestBody);

                }

                //处理请求
                handleRequest(method, path, headers, requestBody, out);
            }

            // 处理请求
            handleRequest(method, path, headers, requestBody, out);

        } catch (SocketException e) {
            // 连接问题只记录一行日志
            System.out.println("连接异常: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(String method, String path,
                               Map<String, String> headers,
                               String requestBody,
                               OutputStream out) throws IOException{
        //处理特殊路径
        if("/test-get".equals(path)){
            handleTestGet(out);
            return;
        }
        if("/test-post".equals(path) && "POST".equalsIgnoreCase(method)){
            handleTestPost(requestBody, out);
            return;
        }
        if("/error-test".equals(path)){
            handleErrorTest(out);
            return;
        }
        if("GET".equalsIgnoreCase(method)){
            serveStaticFile(path, out);
        }else{
            sendErrorResponse(out, 405, "Method not allowed");
        }

    }

    private void handleTestGet(OutputStream out) throws IOException {
        System.out.println("Begin to handle test get request ......");
        String html = "<!DOCTYPE html>\n"+
                "<html>\n"+
                "<head>\n"+
                "    <title> GET请求测试</title>\n"+
                "    <style>\n"+
                "        body { font-family: Arial, snas-serif; margin: 40px;}\n"+
                "        .container {max-width: 800px; margin: 0 auto;}\n"+
                "        .back-link {margin-top:20px;}\n"+
                "    </style>\n"+
                "</head>\n"+
                "<body>\n"+
                "    <div clas=\"container\">\n"+
                "        <h1> GET请求测试成功</h1>\n"+
                "        <p>这是一个GET请求的测试响应。</p>\n"+
                "        <p>当前时间： "+new Date() +"</p>\n"+
                "        <p>服务器信息： Java简易HTTP服务器 v1.0</p>\n"+
                "        <div class=\"back-link\">\n"+
                "            <a href=\"/index.html\">返回首页</a>\n"+
                "        </div>\n"+
                "    </div>\n"+
                "</body>\n"+
                "</html>";
        sendResponse(out, 200, "OK","text/html", html);
    }

    //处理POST请求测试
    private void handleTestPost(String requestBody, OutputStream out) throws IOException {
        String responseText;
        if(requestBody != null && !requestBody.isEmpty()){
            //解析POST数据
            Map<String, String> params = parseFormData(requestBody);
            responseText="POST请求处理成功！\n\n";
            responseText += "接收到的数据：\n";
            for(Map.Entry<String, String> entry : params.entrySet()){
                responseText += entry.getKey() + ": "+entry.getValue()+"\n";
            }
            responseText += "\n处理时间：" + new Date();
        }else{
            responseText = "POST 请求处理成功，但未收到数据。\n\n";
            responseText += "处理时间： "+new Date();
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
        sendResponse(out, 200, "OK","text/html", html);
    }

    private void handleErrorTest(OutputStream out) throws IOException {
        sendErrorResponse(out, 500, "Internal Server Error (for testing purpose)");
    }
//!!  该写这个函数了
    private void serveStaticFile(String path, OutputStream out) throws IOException {
        String filePath = path;
        if("/".equals(path)){
            filePath = "/index.html";
        }

        File file = new File(rootDirectory + filePath);
        if(!file.exists() || !file.isFile() || !file.canRead()){
            sendErrorResponse(out, 404, "File not found");
            return;
        }
        //检查文件是否在根目录内（防止路径遍历攻击）
//        String path1 = file.getPath();
//        Path path2 = file.toPath();
        Path fileRealPath = file.toPath().toRealPath();
        Path directoryRealPath = new File(rootDirectory).toPath().toRealPath();


//        if(!file.getCanonicalPath().startsWith(new File(rootDirectory).getCanonicalPath())){
        if(!fileRealPath.startsWith(directoryRealPath)){
            sendErrorResponse(out, 403, "Forbidden");
            return;
        }

        //确定MIME类型
        String mimeType = "application/octet-stream";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex > 0){
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            mimeType = MIME_TYPE.getOrDefault(extension, "application/octet-stream");
        }

        //读取文件内容
        byte[] fileContent = Files.readAllBytes(file.toPath());

        //send response
        sendResponse(out, 200, "OK",mimeType, fileContent);

    }

    //发送HTTP响应
    private void sendResponse(OutputStream out, int statusCode, String statusMessage,
                              String contentType,String content) throws IOException {
        sendResponse(out, statusCode, statusMessage, contentType, content.getBytes(StandardCharsets.UTF_8));
    }

    private void sendResponse(OutputStream out, int statusCode, String statusMessage,
                              String contentType,byte[] content) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        //这个writer  和 out什么关系？？？？

        //发送状态行
        writer.println("HTTP/1.1 "+statusCode+" "+statusMessage);

        //发送响应头
        writer.println("Content-Type: "+ contentType+"; charset=utf-8");
        writer.println("Content-Length: "+content.length);
        writer.println("Connection: close");
        writer.println("Server: SimpleHttpServer/1.0");
        writer.println();//空行分割头部和主体

        writer.flush();//确保头部已经发送
        //头部发送到哪里？？  -- out

        //发送响应主体
        out.write(content);
        out.flush();
    }

    //发送错误响应
    private void sendErrorResponse(OutputStream out, int statusCode, String statusMessage) throws IOException {
        String title, description;
        switch(statusCode){
            case 400:
                title = "400 Bad Request";
                description="服务器无法理解请求的语法。";
                break;
            case 403:
                title="403 Forbidden";
                description="服务器拒绝请求。";
                break;
            case 404:
                title="404 Not Found";
                description="服务器找不到请求的资源。";
                break;
            case 405:
                title="405 Method not Allowed";
                description="请求方法不被允许";
                break;
            case 500:
                title="500 Internal Server Error";
                description="服务器遇到错误，无法完成请求";
                break;
            default:
                title=statusCode+" Error";
                description="发生错误。";
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
        sendResponse(out, statusCode, statusMessage, "content/html", html);
    }

    //解析表单数据
    private Map<String, String> parseFormData(String formData){
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            int idx = pair.indexOf("=");
            if(idx > 0){
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx+1), StandardCharsets.UTF_8);
                //为什么用URLDecoder.decode， 直接用pair.substring(0, idx)不行吗？
                //A:解码器，为了还原浏览器输入的原始数据
                /*
                在HTTP请求中，表单数据需要进行URL编码（也叫百分号编码），原因包括
                特殊字符处理：

                java
                // 原始表单数据
                用户名：张三
                邮箱：user@example.com
                消息：hello&world=test

                // URL编码后（实际传输的数据）
                username=%E5%BC%A0%E4%B8%89&email=user%40example.com&message=hello%26world%3Dtest
                 */

                /*
                原始表单数据：name=%E5%BC%A0%E4%B8%89&email=test%40example.com&msg=Hello%20World%26Test

                --- 错误解析（不解码） ---直接用pair.substring(0, idx)
                name = %E5%BC%A0%E4%B8%89
                email = test%40example.com
                msg = Hello%20World%26Test

                --- 正确解析（解码） ---用URLDecoder.decode
                name = 张三
                email = test@example.com
                msg = Hello World&Test
                 */
                params.put(key, value);
            }
        }
        return params;
    }
}
