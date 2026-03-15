package com.practice.downloader;


import java.util.Scanner;
/**
 * @author: Linda
 * @date: 2026/3/5 10:27
 * @description:
 */
public class ConsoleDownloadUI {
    private MultiThreadDownloader downloader;
    private Scanner scanner;

    public ConsoleDownloadUI() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== 多线程下载器 ===");

        // 获取下载信息
        System.out.print("请输入下载URL: ");
        String url = scanner.nextLine();

        System.out.print("请输入保存路径: ");
        String savePath = scanner.nextLine();

        System.out.print("请输入线程数 (默认5): ");
        String threadInput = scanner.nextLine();
        int threadCount = threadInput.isEmpty() ? 5 : Integer.parseInt(threadInput);

        // 创建下载器
        downloader = new MultiThreadDownloader(url, savePath, threadCount);
        downloader.setListener(createListener());

        // 启动下载
        try {
            downloader.start();
            showControlMenu();
        } catch (Exception e) {
            System.out.println("启动下载失败: " + e.getMessage());
        }
    }

    private DownloadListener createListener() {
        return new DownloadListener() {
            @Override
            public void onProgress(long downloaded, long total,
                                   int speed, String remainingTime) {
                System.out.printf("\r[%-50s] %.2f%% | 速度: %s/s | 剩余: %s",
                        getProgressBar(downloaded, total, 50),
                        downloaded * 100.0 / total,
                        formatSpeed(speed),
                        remainingTime);
            }

            @Override
            public void onComplete(String filePath) {
                System.out.println("\n\n下载完成！文件保存在: " + filePath);
            }

            @Override
            public void onPaused() {
                System.out.println("\n\n下载已暂停");
            }

            @Override
            public void onResumed() {
                System.out.println("\n\n下载已恢复");
            }

            @Override
            public void onCancelled() {
                System.out.println("\n\n下载已取消");
            }

            @Override
            public void onError(String error) {
                System.out.println("\n\n错误: " + error);
            }

            private String getProgressBar(long downloaded, long total, int length) {
                int progress = (int) (downloaded * length / total);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    sb.append(i < progress ? '=' : ' ');
                }
                return sb.toString();
            }

            private String formatSpeed(int bytesPerSecond) {
                if (bytesPerSecond < 1024) return bytesPerSecond + "B";
                if (bytesPerSecond < 1024 * 1024)
                    return String.format("%.1fKB", bytesPerSecond / 1024.0);
                return String.format("%.1fMB", bytesPerSecond / (1024.0 * 1024.0));
            }
        };
    }

    private void showControlMenu() {
        System.out.println("\n\n控制命令: [P]暂停 [R]恢复 [C]取消 [Q]退出");

        while (true) {
            System.out.print("\n请输入命令: ");
            String command = scanner.nextLine().toUpperCase();

            switch (command) {
                case "P":
                    downloader.pause();
                    break;
                case "R":
                    downloader.resume();
                    break;
                case "C":
                    downloader.cancel();
                    return;
                case "Q":
                    downloader.cancel();
                    System.out.println("程序退出");
                    return;
                default:
                    System.out.println("未知命令");
            }
        }
    }

    public static void main(String[] args) {
        new ConsoleDownloadUI().start();
    }
}
