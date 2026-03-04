package com.practice.basichttpserver.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
/**
 * @author: Linda
 * @date: 2026/3/4 13:59
 * @description:
 */
public class SimpleHttpServer {
    private static final Logger logger = Logger.getLogger(SimpleHttpServer.class.getName());
    private final int port;
    private final String webRoot;
    private final ExecutorService threadPool;
    private volatile boolean isRunning = true;

    public SimpleHttpServer(int port, String webRoot) {
        this.port = port;
        this.webRoot = webRoot;
        this.threadPool = Executors.newFixedThreadPool(10); // 10个线程的线程池
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("HTTP服务器启动在端口: " + port);
            logger.info("静态文件目录: " + new File(webRoot).getAbsolutePath());

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(5000); // 5秒超时

                    // 将请求提交给线程池处理
                    threadPool.submit(new RequestHandler(clientSocket, webRoot));
                } catch (IOException e) {
                    if (isRunning) {
                        logger.severe("接受客户端连接失败: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("服务器启动失败: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        isRunning = false;
        threadPool.shutdown();
        logger.info("服务器已关闭");
    }
}