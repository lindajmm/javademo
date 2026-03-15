package com.practice.downloader;

import java.io.*;
        import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;
/**
 * @author: Linda
 * @date: 2026/3/5 10:24
 * @description:
 */
public class DownloadTask implements Runnable,Serializable {
    private int threadId;
    private String url;
    private String savePath;
    private long startPos;
    private long endPos;
    private AtomicLong downloaded;
    private volatile boolean isRunning = true;
    private volatile boolean isPaused = false;

    public DownloadTask(int threadId, String url, String savePath,
                        long startPos, long endPos) {
        this.threadId = threadId;
        this.url = url;
        this.savePath = savePath;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloaded = new AtomicLong(0);
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        InputStream is = null;

        try {
            // 检查是否已有下载进度
            long actualStart = startPos + downloaded.get();
            if (actualStart > endPos) {
                System.out.println("线程 " + threadId + " 已完成下载");
                return;
            }

            URL downloadUrl = new URL(url);
            conn = (HttpURLConnection) downloadUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 设置下载范围
            if (endPos > 0) {
                conn.setRequestProperty("Range", "bytes=" + actualStart + "-" + endPos);
                System.out.println("线程 " + threadId + " 下载区间: " +
                        actualStart + "-" + endPos);
            }

            int responseCode = conn.getResponseCode();

            // 检查响应码（206表示部分内容，支持断点续传）
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_PARTIAL) {

                System.out.println("response code is "+ responseCode);

                is = conn.getInputStream();
                raf = new RandomAccessFile(savePath, "rw");
                raf.seek(actualStart);

                byte[] buffer = new byte[1024 * 1024]; // 1MB缓冲区
                int len;
                long totalRead = downloaded.get();

                while (isRunning && (len = is.read(buffer)) != -1) {
                    // 检查是否暂停
                    while (isPaused && isRunning) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    if (!isRunning) {
                        break;
                    }

                    raf.write(buffer, 0, len);
                    totalRead += len;
                    downloaded.set(totalRead);
//                    System.out.println("******"+Thread.currentThread().getName() + "下载 "+ totalRead+ "byte.");

                    // 检查是否到达结束位置
                    if (endPos > 0 && (startPos + totalRead) >= endPos) {
                        break;
                    }
                }

                System.out.println("线程 " + threadId + " 下载完成");
            } else {
                System.out.println("线程 " + threadId + " 服务器响应异常: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("线程 " + threadId + " 下载出错: " + e.getMessage());
        } finally {
            try {
                if (is != null) is.close();
                if (raf != null) raf.close();
                if (conn != null) conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        synchronized (this) {
            notify();
        }
    }

    public void cancel() {
        isRunning = false;
        isPaused = false;
        synchronized (this) {
            notify();
        }
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    public int getThreadId() {
        return threadId;
    }

    public long getStartPos() {
        return startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded.set(downloaded);
    }
}
