package com.practice.downloadv02;


import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Linda
 * @date: 2026/3/6 14:17
 * @description:
 */
public class DownloadTask implements Runnable {
    private int threadId;
    private String sourceURL; // e.g. http://www.baidu.com
    private String targetFilePath;// e.g. D:\downloads
    private long start;
    private long end;
    private RandomAccessFile raf;
    private AtomicLong downloaded;


    public DownloadTask(int threadId, String sourceURL, String targetFilePath, long start, long end) {
        this.threadId = threadId;
        this.sourceURL = sourceURL;
        this.targetFilePath = targetFilePath;
        this.start = start;
        this.end = end;
        this.downloaded = new AtomicLong(0);
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        RandomAccessFile raf = null;
        try {
            System.out.println("线程：" + threadId + "开始工作，下载范围从" + start + " 至 " + end);
            URL url = new URL(sourceURL);

            // 检查是否已有下载进度
            long actualStart = start + downloaded.get();
            System.out.println("actual start is " + actualStart+ "start is "+start);
            if (actualStart > end) {
                System.out.println("线程 " + threadId + " 已完成下载");
                return;
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
//            conn.setRequestProperty("Range", "bytes=" + actualStart + "-" + endPos);

            String byteRange = "bytes=" + start + "-" + end;
            urlConnection.setRequestProperty("Range", byteRange);

            int responseCode = urlConnection.getResponseCode();
            System.out.println("response code is "+ responseCode);
            if (responseCode == HttpURLConnection.HTTP_PARTIAL || responseCode == HttpURLConnection.HTTP_OK) {
//                System.out.println("服务器可能支持断点续传，也可能不支持");
                long totalRead = downloaded.get();
                System.out.println("initial value of downloaded is "+ totalRead);
                //该服务器支持断点续传
                inputStream = urlConnection.getInputStream();

                raf = new RandomAccessFile(targetFilePath, "rw");
//                raf.seek(start);
                raf.seek(actualStart);
                byte[] buffer = new byte[1024 * 1024];
                int len;

                while ((len = inputStream.read(buffer)) != -1) {
                    raf.write(buffer, 0, len);
                    totalRead += len;
                    downloaded.set(totalRead);

//                    System.out.println("线程 " + threadId + " 下载 "+ formatSize(totalRead));

                    // 检查是否到达结束位置
                    if (end > 0 && (start + totalRead) >= end) {
                        break;
                    }
                }

                System.out.println("线程 " + threadId + " 下载完成");
            } else {
                System.out.println("服务器未正确响应！！" + "response code is " + responseCode);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {

                if (inputStream != null) {
                    inputStream.close();
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024)
            return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024)
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}
