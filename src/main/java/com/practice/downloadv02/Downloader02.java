package com.practice.downloadv02;


import com.practice.downloader.MultiThreadDownloader;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Linda
 * @date: 2026/3/6 14:16
 * @description:
 */
public class Downloader02 {
    private String sourceURL; // e.g. http://www.baidu.com
    private String targetFilePath;// e.g. D:\downloads
    private int threadNum = 5;

    private long fileSize;
    private boolean supportRange;
    private String fileName;
    private String file;
    private List<DownloadTask> tasks;

    private DownloadListener02 downloadListener;

    // 下载状态
    private volatile boolean isDownloading = false;



    public Downloader02(String sourceURL, String targetFilePath) {
        this.sourceURL = sourceURL;
        this.targetFilePath = targetFilePath;
        this.tasks = new ArrayList<>();

    }

     void setDownloadListener(DownloadListener02 listener){
        this.downloadListener = listener;
    }

    public void downloadFile() throws IOException {
        fileName = getFileName();
        checkIfSupportRange();
      /*  if(downloadListener != null){
//            downloadListener.onProgress(0);
        }*/
        prepareFile();

        URL url = new URL(sourceURL);
        //begin to get the content of the source file
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //获取响应数据流之后，就是要读取，然后保存到本地的文件中
        File targetDir = new File(targetFilePath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        file = targetFilePath + File.separator + fileName;
//        List<DownloadTask> tasks = new ArrayList<>();
        ExecutorService excutor = Executors.newFixedThreadPool(threadNum);

        isDownloading = true;

        tasks.clear();

        try (InputStream inputStream = urlConnection.getInputStream()
            /* RandomAccessFile raf = new RandomAccessFile(file, "rw")*/) {

            long blockSize = fileSize / threadNum;
            for (int i = 0; i < threadNum; i++) {
                long start;
                long end;

                start = i * blockSize;
                if (i == threadNum - 1) {
                    end = fileSize -1;
                } else {
                    end = (i + 1) * blockSize - 1;
                }

                DownloadTask downloadTask =
                        new DownloadTask(i, sourceURL, file, start, end);
                tasks.add(downloadTask);
                excutor.execute(downloadTask);
            }

           /* for (DownloadTask task : tasks) {
                excutor.submit(task);
            }*/

            excutor.shutdown();

//            monitorProgress();
            Thread monitorThread = new Thread(new ProgressMonitor());
//            monitorThread.setDaemon(true);
            monitorThread.start();


            excutor.awaitTermination(10, TimeUnit.MINUTES);

            // 等待监控线程完成最后一次更新
//            Thread.sleep(500);
//            System.out.println("多线程下载完成。");
        } catch (IOException  e) {
            if(downloadListener != null){
                downloadListener.onError(e.getMessage());
            }
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }


    public String getFileName() {
        //get file name
        String fileName;
        int i = sourceURL.lastIndexOf('/');
        String substring = sourceURL.substring(i + 1);
        if (substring.contains("?")) {
            String[] splits = substring.split("\\?");
            fileName = splits[0];
        } else {
            fileName = substring;
        }
        return fileName;
    }

    public void checkIfSupportRange() throws MalformedURLException {
        URL url = new URL(sourceURL);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");

            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            if (responseCode == 200 && responseMessage.equals("OK")) {
                fileSize = con.getContentLengthLong();
                String ranges = con.getHeaderField("Accept-Ranges");
                supportRange = "bytes".equals(ranges);
                //如果不支持断点续传，则只能用单线程实现
                if(!supportRange){
                    threadNum =1;
                }
                System.out.println("**********thread num is "+threadNum);
            } else {
                System.out.println("Error code is " + responseCode + ", error message is " + responseMessage);
                throw new RuntimeException("Failed to access the file, please check the path!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            con.disconnect();
        }
    }

    public void prepareFile() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.setLength(fileSize);
        }
    }

  /*  private void monitorProgress() throws InterruptedException {
        while(totalDownloaded.get() < fileSize){
            int percent = (int) (totalDownloaded.get()*100/fileSize);
            System.out.println("已完成下载 "+percent + "%.");
            Thread.sleep(1000);
        }
    }*/

    /**
     * 下载进度监控器
     */
    private class ProgressMonitor implements Runnable {
        @Override
        public void run() {
            long lastDownloaded = 0;
            long lastTime = System.currentTimeMillis();
            while (isDownloading) {
                try {
                    Thread.sleep(1000); // 每秒更新一次

                    long downloaded = getTotalDownloaded();
                    long currentTime = System.currentTimeMillis();

                    // 计算下载速度
                    long timeDiff = currentTime - lastTime;
                    long dataDiff = downloaded - lastDownloaded;
                    double speed = dataDiff * 1000.0 / timeDiff;

                    // 计算剩余时间
                    long remaining = fileSize - downloaded;
                    long remainingTime = (long) (remaining / speed * 1000);

                    // 更新进度
                    if (downloadListener != null) {
                        downloadListener.onProgress(downloaded, fileSize,
                                (int) speed, formatTime(remainingTime));
                    }

                    // 保存当前状态
                    lastDownloaded = downloaded;
                    lastTime = currentTime;

                    // 检查是否完成
                    if (downloaded >= fileSize) {
                        completeDownload();
                        break;
                    }


                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String formatTime(long millis) {
            if (millis <= 0) return "未知";

            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            if (hours > 0) {
                return String.format("%d小时%d分钟", hours, minutes % 60);
            } else if (minutes > 0) {
                return String.format("%d分钟%d秒", minutes, seconds % 60);
            } else {
                return String.format("%d秒", seconds);
            }
        }

        private void completeDownload() throws IOException {
            isDownloading = false;
            System.out.println("所有文件下载完成！");
            if (downloadListener != null) {
                downloadListener.onComplete(file);
            }
//            isDownloading = false;

            // 重命名临时文件
           /* File tempFile = new File(savePath, fileName + TEMP_SUFFIX);
            File finalFile = new File(savePath, fileName);

            if (tempFile.renameTo(finalFile)) {
                // 删除信息文件
                File infoFile = new File(savePath, fileName + INFO_SUFFIX);
                if (infoFile.exists()) {
                    infoFile.delete();
                }

                if (listener != null) {
                    listener.onComplete(finalFile.getAbsolutePath());
                }
            } else {
                throw new IOException("文件重命名失败");
            }*/
        }
    }

    /**
     * 计算总下载量
     */
    private long getTotalDownloaded() {
        long total = 0;
        for (DownloadTask task : tasks) {
            total += task.getDownloaded();
        }
        return total;
    }
}
