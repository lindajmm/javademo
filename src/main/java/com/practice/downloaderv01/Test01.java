package com.practice.downloaderv01;


import java.net.MalformedURLException;

/**
 * @author: Linda
 * @date: 2026/3/6 12:31
 * @description:
 */
public class Test01 {
    public static void main(String[] args) throws MalformedURLException {
        Downloader01 downloader01 = new Downloader01("http://www.baidu.com/index.html", "D:\\downloads");
        downloader01.downloadFile();
    }
}
