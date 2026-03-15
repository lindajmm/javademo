package com.practice.downloadv02;


/**
 * @author: Linda
 * @date: 2026/3/9 14:09
 * @description:
 */
public interface DownloadListener02 {

    void onProgress(long downloaded, long total,
                    int speed, String remainingTime);
    void onComplete(String filePath);

    void onSuccess(String filePath);

    void onError(String errorMessage);
}
