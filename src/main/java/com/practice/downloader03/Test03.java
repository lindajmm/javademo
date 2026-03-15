package com.practice.downloader03;


import java.io.File;
import java.io.IOException;

/**
 * @author: Linda
 * @date: 2026/3/10 15:04
 * @description:
 */
public class Test03 {
    static String  downloadUrl = "https://ftp.nluug.nl/pub/test/100mb.bin";
    static String saveDirect = "D:/downloads";

    public static void main(String[] args) {
        String fileName;
        int lastIndex = downloadUrl.lastIndexOf("/");
        String lastItem = downloadUrl.substring(lastIndex + 1);
        if(lastItem.contains("\\?")){
            String[] splits = lastItem.split("\\?");
            fileName = splits[0];
        }else{
            fileName=lastItem;
        }
        File file = new File(saveDirect);
        if(!file.exists()){
            file.mkdirs();
        }
        String saveFilePath = saveDirect + File.separator + fileName;

        Downloader03 downloader03 = new Downloader03(downloadUrl, saveFilePath, 5);

        downloader03.setListener(new DownloadListener03() {
            @Override
            public void onProgress(double downloadedPercent, double speed, double leftTime, long downloadedSize) {
                System.out.printf("\r下载进度： %.2f %s | 速度： %-8s | 剩余时间： %-8s | 已下载： %-8s",
                        downloadedPercent,"%", formateSpeed(speed), formateLeftTime(leftTime), formateDownloadedSIze(downloadedSize));
            }

            String formateSpeed(double speed){
                if(speed < 1024){
                    return String.format("%.2f B/s", speed);
                }
                if(speed < 1024*1024){
                    return String.format("%.2f KB/s", speed/1024);
                }
                if(speed < 1024*1024*1024){
                    return String.format("%.2f MB/s", speed/(1024*1024));
                }
                return String.format("%.2f GB/s", speed/(1024*1024*1024));
            }

            String formateLeftTime(double leftTime){
                if(leftTime < 59){
                    return String.format("%.0f 秒", leftTime);
                }else if(leftTime >= 60 && leftTime < 60*60){
                    return String.format("%.1f 分钟%.1f秒", leftTime/60, leftTime%60);
                }else if(leftTime >= 60*60 && leftTime < 60*60*60){
                    double time = leftTime%3600 < 60 ? 0: (leftTime%3600)/60;
                    return String.format("%.1f小时 %.1f分钟", leftTime/3600,time);
                }else{
                    return "unknown";
                }
            }

            String formateDownloadedSIze(long downloaded){
                if(downloaded < 1024){
                    return String.format("%s B", downloaded);
                }
                if(downloaded < 1024*1024){
                    return String.format("%.2f KB", downloaded/1024.0);
                }
                if(downloaded < 1024*1024*1024){
                    return String.format("%.2f MB", downloaded/(1024.0*1024.0));
                }
                 return String.format("%.2f GB", downloaded/(1024.0*1024.0*1024.0));

            }


            @Override
            public void onCompleted(String downloadedPath) {
                System.out.println("文件下载完成，下载路径是 "+ downloadedPath);
            }

            @Override
            public void onError(String message) {
                System.out.println("下载错误： "+ message);
            }
        });

        try {
            downloader03.downloadFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
