package com.practice.downloader;


/**
 * @author: Linda
 * @date: 2026/3/5 10:27
 * @description:
 */

public class DownloadTest {
    public static void main(String[] args) {
        // 测试下载
//        String downloadUrl = "https://example.com/testfile.zip";
//        String downloadUrl = "https://www.baidu.com/index.html";
        String downloadUrl = "https://ftp.nluug.nl/pub/test/100mb.bin";
        String savePath = "D:/downloads";
        int threadCount = 5;

        MultiThreadDownloader downloader = new MultiThreadDownloader(
                downloadUrl, savePath, threadCount);

        // 设置监听器
        downloader.setListener(new DownloadListener() {
            @Override
            public void onProgress(long downloaded, long total,
                                   int speed, String remainingTime) {
                System.out.printf("\r下载进度: %.2f%% | " +
                                "速度: %s/s | 剩余时间: %s | 已下载: %s",
                        downloaded * 100.0 / total,
                        formatSpeed(speed),
                        remainingTime,
                        formatSize(downloaded));
            }

            @Override
            public void onComplete(String filePath) {
                System.out.println("\n下载完成！文件保存路径: " + filePath);
            }

            @Override
            public void onPaused() {
                System.out.println("\n下载已暂停");
            }

            @Override
            public void onResumed() {
                System.out.println("\n下载已恢复");
            }

            @Override
            public void onCancelled() {
                System.out.println("\n下载已取消");
            }

            @Override
            public void onError(String error) {
                System.out.println("\n下载错误: " + error);
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

        // 启动下载
        try {
            downloader.start();

//            // 模拟用户操作
//            Thread.sleep(500); // 下载5秒
//            downloader.pause();  // 暂停
//            Thread.sleep(15000); // 暂停3秒
//            downloader.resume(); // 恢复
//            Thread.sleep(100); // 再下载5秒
//            downloader.pause();  // 暂停
//            Thread.sleep(15000); // 暂停3秒
//
//            downloader.cancel(); // 取消

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
