package com.practice.downloader;

public interface DownloadListener {
    /**
     * 进度更新
     * @param downloaded 已下载字节数
     * @param total 总字节数
     * @param speed 下载速度（字节/秒）
     * @param remainingTime 剩余时间
     */
    void onProgress(long downloaded, long total, int speed, String remainingTime);

    /**
     * 下载完成
     * @param filePath 文件保存路径
     */
    void onComplete(String filePath);

    /**
     * 下载暂停
     */
    void onPaused();

    /**
     * 下载恢复
     */
    void onResumed();

    /**
     * 下载取消
     */
    void onCancelled();

    /**
     * 下载错误
     * @param error 错误信息
     */
    void onError(String error);
}
