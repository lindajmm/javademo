package com.practice.downloadv02;


import java.io.IOException;

/**
 * @author: Linda
 * @date: 2026/3/6 14:14
 * @description: 多线程下载一个文件，避免大文件下载时间太长
 */
public class Test02 {

    String downloadUrl = "https://ftp.nluug.nl/pub/test/100mb.bin";
    String savePath = "D:/downloads";
    public static void main(String[] args) {
        try {


            Downloader02 downloader02 =
                    new Downloader02("https://ftp.nluug.nl/pub/test/100mb.bin", "D:\\downloads");


            downloader02.setDownloadListener(new DownloadListener02() {
                @Override
                public void onProgress(long downloaded, long total,
                                       int speed, String remainingTime) {

                    /*System.out.println("download progress is " + percent + "%.");
                    if (percent == 50) {
                        System.out.println("Downloaded a half of the file");
                    }*/

                    System.out.printf("\r下载进度: %.2f%% | " +
                                    "速度: %s/s | 剩余时间: %s | 已下载: %s",
                            downloaded * 100.0 / total,
                            formatSpeed(speed),
                            remainingTime,
                            formatSize(downloaded));
                }

                @Override
                public void onComplete(String filePath) {
                    System.out.println("Downloading completed. It has been saved to " + filePath);
                }

                @Override
                public void onSuccess(String filePath) {
                    System.out.println("Downloaded successfully. It has been saved to " + filePath);
                }

                @Override
                public void onError(String errorMessage) {
                    System.out.println("Failed to download the file, error message is " + errorMessage);

                }

                private String formatSpeed(int bytesPerSecond) {
                    if (bytesPerSecond < 1024) return bytesPerSecond + " B";
                    if (bytesPerSecond < 1024 * 1024)
                        return String.format("%.2f KB", bytesPerSecond / 1024.0);
                    return String.format("%.2f MB", bytesPerSecond / (1024.0 * 1024.0));
                }

                private String formatSize(long size) {
                    if (size < 1024) return size + " B";
                    if (size < 1024 * 1024)
                        return String.format("%.2f KB", size / 1024.0);
                    if (size < 1024 * 1024 * 1024)
                        return String.format("%.2f MB", size / (1024.0 * 1024.0));
                    return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
                }
            });
            downloader02.downloadFile();
        }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
