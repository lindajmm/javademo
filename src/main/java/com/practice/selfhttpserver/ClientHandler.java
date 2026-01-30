package com.practice.selfhttpserver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Linda
 * @date: 2026/1/30 17:14
 * @description:
 */
public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private String rootDirectory = "webroot";

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
//                sendErrorResponse(out, 400, "Bad request");
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
                if(headers.containsKey("content-length")){
                    contentLength = Integer.parseInt(headers.get("content-length"));
                }

                if(contentLength >0){
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars, 0, contentLength);
                    requestBody = new String(bodyChars);
                }

                //处理请求
                handleRequest(method, path, headers, requestBody, out);
            }

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

        }
        if("/test-post".equals(path) && "POST".equalsIgnoreCase(method)){

        }
        if("/error-test".equals(path)){

        }
        if("GET".equalsIgnoreCase(method)){

        }else{

        }

    }
}
