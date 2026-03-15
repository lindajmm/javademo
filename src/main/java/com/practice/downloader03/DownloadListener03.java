package com.practice.downloader03;


/**
 * @author: Linda
 * @date: 2026/3/10 14:40
 * @description:
 */
public interface DownloadListener03 {
    void onProgress(double downloadedPercent, double speed, double leftTime, long downloadedSize);

    void onCompleted(String downloadedPath);

    void onError(String message);
}
