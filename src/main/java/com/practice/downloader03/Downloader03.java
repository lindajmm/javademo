package com.practice.downloader03;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Linda
 * @date: 2026/3/10 14:39
 * @description:
 */
public class Downloader03 {
    private String fileURL;
    private String downloadedPath;
    private int threadCount;

    private DownloadListener03 downloadListener03;
    private long fileSize;
    private List<DownloadTask03> tasks = new ArrayList<>();

    private AtomicLong allDownloaded = new AtomicLong(0);

    public Downloader03(String fileURL, String downloadedPath, int threadCount) {
        this.fileURL = fileURL;
        this.downloadedPath = downloadedPath;
        this.threadCount = threadCount;
    }

    void setListener(DownloadListener03 downloadListener03) {
        this.downloadListener03 = downloadListener03;
    }

    public void downloadFile() throws IOException {
        try {
            init();
            prepareFile();
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            System.out.println("file size is "+ fileSize);

//        107/5 = 21, left 2, 0-20, 21-41, 42-62, 63-83, 84 - 107-1
            long blockSize = fileSize / threadCount;
            long startPos;
            long endPos;
            for (int i = 0; i < threadCount; i++) {
                startPos = i * blockSize;
                if (i == threadCount - 1) {
                    endPos = fileSize - 1;
                } else {
                    endPos = (i + 1) * blockSize - 1;
                }
                DownloadTask03 task03 = new DownloadTask03(i, fileURL, downloadedPath, startPos, endPos);
                tasks.add(task03);
            }
            for (DownloadTask03 task : tasks) {
                executor.submit(task);
            }
            executor.shutdown();

            new Thread(new ProgressMonitor()).start();
        }catch (Exception e){
            if(downloadListener03 != null){
                downloadListener03.onError("download error"+ e.getMessage());
            }
            throw new RuntimeException("downloading failed");
        }
    }

    class ProgressMonitor implements Runnable {
        //主要用来监控下载过程

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long lastDownloaded=0;
            while (true) {
                try {
                    Thread.sleep(1000);//每1s 查看一下进度
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                long total = getTotalDownloaded();
//                System.out.println("Progress Monitor allDownloaded is "+ total);

                long endTime = System.currentTimeMillis();
                long downloadedDiff = total-lastDownloaded;
                long timeDiff = endTime - startTime;
                double downloadedPercent = total * 100.0 / fileSize;

                double speed = downloadedDiff * 1000.0 / timeDiff;// byte/s

                double leftTime = (fileSize - total) / speed;
                if (downloadListener03 != null) {
                    downloadListener03.onProgress(downloadedPercent, speed, leftTime, total);
                }
                //保存当前数据
                startTime = endTime;
                lastDownloaded = total;
                //判断是否下载完成
                if (total >= fileSize) {
                    if (downloadListener03 != null) {
                        downloadListener03.onCompleted(downloadedPath);
                        break;
                    }
                }
            }
        }
    }

    private long getTotalDownloaded(){
        long total =0;
        for (DownloadTask03 task : tasks) {
            long downloaded = task.getDownloaded();
            total += downloaded;
        }
        return total;
    }

    private void init() {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
                fileSize = con.getContentLengthLong();
                String acceptRange = con.getHeaderField("Accept-Ranges");
                if (!"bytes".equals(acceptRange)) {
                    this.setThreadCount(1);// 不太确定这样的实现是不是正确？
                }/*else{
                    this.setThreadCount(1);
                }*/
            } else {
                throw new RuntimeException("Failed to get a correct response code, please check the given file url.");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void prepareFile() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(downloadedPath, "rw");
        raf.setLength(fileSize);
        raf.close();
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}
