package com.practice.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: Linda
 * @date: 2026/3/5 10:23
 * @description:
 */
public class MultiThreadDownloader {
    // 下载配置
    private String downloadUrl;
    private String savePath;
    private String fileName;
    private int threadCount;
    private long fileSize;
    private boolean supportRange;

    // 下载状态
    private volatile boolean isDownloading = false;
    private volatile boolean isPaused = false;
    private DownloadInfo downloadInfo;
    private List<DownloadTask> tasks;
    private DownloadListener listener;

    // 临时文件后缀
    private static final String TEMP_SUFFIX = ".tmp";
    private static final String INFO_SUFFIX = ".info";

    public MultiThreadDownloader(String downloadUrl, String savePath, int threadCount) {
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.threadCount = threadCount;
        this.tasks = new ArrayList<>();

        // 从URL中提取文件名
        String urlPath = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        this.fileName = urlPath.contains("?") ?
                urlPath.substring(0, urlPath.indexOf("?")) : urlPath;
        if (this.fileName.isEmpty()) {
            this.fileName = "download_" + System.currentTimeMillis();
        }
    }

    /**
     * 开始下载
     */
    public void start() throws IOException, InterruptedException {
        if (isDownloading) {
            return;
        }

        // 检查是否支持断点续传
        checkDownloadSupport();

        // 创建下载目录
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        // 检查是否有未完成的下载
        File infoFile = new File(savePath, fileName + INFO_SUFFIX);
        if (infoFile.exists()) {
            // 加载之前的下载信息
            loadDownloadInfo();
            if (downloadInfo != null && downloadInfo.getFileSize() == fileSize) {
                resumeDownload();
                return;
            }
        }

        // 开始新下载
        startNewDownload();
    }

    /**
     * 暂停下载
     */
    public void pause() {
        if (isDownloading && !isPaused) {
            isPaused = true;
            for (DownloadTask task : tasks) {
                task.pause();
            }
            saveDownloadInfo();
            if (listener != null) {
                listener.onPaused();
            }
        }
    }

    /**
     * 恢复下载
     */
    public void resume() {
        if (isPaused) {
            isPaused = false;

            for (DownloadTask task : tasks) {
                task.resume();  // 调用每个task自己的resume()
            }
            synchronized (this) {
                notifyAll();
            }
            if (listener != null) {
                listener.onResumed();
            }
        }
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (isDownloading) {
            isDownloading = false;
            isPaused = false;
            for (DownloadTask task : tasks) {
                task.cancel();
            }

            // 清理临时文件
            cleanup();

            if (listener != null) {
                listener.onCancelled();
            }
        }
    }

    /**
     * 检查下载支持
     */
    private void checkDownloadSupport() throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                fileSize = conn.getContentLengthLong();
                String acceptRanges = conn.getHeaderField("Accept-Ranges");
                supportRange = "bytes".equals(acceptRanges);

                if (!supportRange) {
                    threadCount = 1; // 不支持断点续传，使用单线程
                }
            } else {
                throw new IOException("服务器响应异常: " + responseCode);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 开始新下载
     */
    private void startNewDownload() throws IOException {
        System.out.println("开始新下载，文件大小: " + formatFileSize(fileSize));
        System.out.println("支持断点续传: " + supportRange);
        System.out.println("使用线程数: " + threadCount);

        isDownloading = true;

        // 创建临时文件
        File tempFile = new File(savePath, fileName + TEMP_SUFFIX);
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
//        raf.setLength(fileSize);
        raf.close();

        // 计算每个线程的下载区间
        long blockSize = fileSize / threadCount;
        tasks.clear();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long startPos = i * blockSize;
            long endPos = (i == threadCount - 1) ? fileSize - 1 : (i + 1) * blockSize - 1;

            DownloadTask task = new DownloadTask(i, downloadUrl,
                    tempFile.getAbsolutePath(), startPos, endPos);
            tasks.add(task);
            executor.execute(task);
        }

        executor.shutdown();

        // 启动进度监控线程
        new Thread(new ProgressMonitor()).start();
    }

    /**
     * 恢复下载
     */
    private void resumeDownload() throws InterruptedException {
        System.out.println("恢复下载，文件大小: " + formatFileSize(fileSize));
        System.out.println("上次下载进度: " +
                formatProgress(downloadInfo.getDownloadedBytes(), fileSize));

        isDownloading = true;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (DownloadTask task : tasks) {
            executor.execute(task);
        }

        executor.shutdown();

        // 启动进度监控线程
        new Thread(new ProgressMonitor()).start();

        while(!executor.isTerminated()){
            Thread.sleep(1000);
        }
    }

    /**
     * 保存下载信息
     */
    private void saveDownloadInfo() {
        if (downloadInfo == null) {
            downloadInfo = new DownloadInfo();
        }

        downloadInfo.setUrl(downloadUrl);
        downloadInfo.setFileName(fileName);
        downloadInfo.setSavePath(savePath);
        downloadInfo.setFileSize(fileSize);
        downloadInfo.setThreadCount(threadCount);
        downloadInfo.setTasks(tasks);

        downloadInfo.saveToFile(savePath + File.separator + fileName + INFO_SUFFIX);
    }

    /**
     * 加载下载信息
     */
    private void loadDownloadInfo() {
        downloadInfo = DownloadInfo.loadFromFile(
                savePath + File.separator + fileName + INFO_SUFFIX);
        if (downloadInfo != null) {
            this.tasks = downloadInfo.getTasks();
        }
    }

    /**
     * 清理临时文件
     */
    private void cleanup() {
        File tempFile = new File(savePath, fileName + TEMP_SUFFIX);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        File infoFile = new File(savePath, fileName + INFO_SUFFIX);
        if (infoFile.exists()) {
            infoFile.delete();
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

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024)
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * 格式化进度
     */
    private String formatProgress(long downloaded, long total) {
        return String.format("%.2f%%", downloaded * 100.0 / total);
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    /**
     * 下载进度监控器
     */
    private class ProgressMonitor implements Runnable {
        @Override
        public void run() {
            long lastDownloaded = 0;
            long lastTime = System.currentTimeMillis();

            while (isDownloading && !Thread.currentThread().isInterrupted()) {
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
                    if (listener != null) {
                        listener.onProgress(downloaded, fileSize,
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

                    // 检查是否暂停
                    while (isPaused && isDownloading) {
                        synchronized (MultiThreadDownloader.this) {
                            MultiThreadDownloader.this.wait();
                        }
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String formatTime(long millis) {
            if (millis < 0) return "未知";
            if (millis == 0) return "0";

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

            // 重命名临时文件
            File tempFile = new File(savePath, fileName + TEMP_SUFFIX);
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
            }
        }
    }
}
