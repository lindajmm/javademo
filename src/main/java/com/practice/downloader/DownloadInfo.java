package com.practice.downloader;

import java.io.*;
import java.util.List;
/**
 * @author: Linda
 * @date: 2026/3/5 10:25
 * @description:
 */
public class DownloadInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;
    private String fileName;
    private String savePath;
    private long fileSize;
    private int threadCount;
    private List<DownloadTask> tasks;
    private long lastUpdateTime;

    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DownloadInfo loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (DownloadInfo) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getDownloadedBytes() {
        long total = 0;
        if (tasks != null) {
            for (DownloadTask task : tasks) {
                total += task.getDownloaded();
            }
        }
        return total;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getSavePath() { return savePath; }
    public void setSavePath(String savePath) { this.savePath = savePath; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }

    public List<DownloadTask> getTasks() { return tasks; }
    public void setTasks(List<DownloadTask> tasks) { this.tasks = tasks; }

    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
