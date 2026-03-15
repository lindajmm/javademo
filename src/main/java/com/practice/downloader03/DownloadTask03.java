package com.practice.downloader03;


import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Linda
 * @date: 2026/3/10 14:40
 * @description:
 */
public class DownloadTask03 implements Runnable{
    private int threadId;
    private String fileURL;
    private String downloadPath;
    private long startPos;
    private long endPos;
    private AtomicLong downloaded;

    public DownloadTask03(int threadId, String fileURL, String downloadPath, long startPos, long endPos) {
        this.threadId = threadId;
        this.fileURL = fileURL;
        this.downloadPath = downloadPath;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloaded = new AtomicLong(0);
    }

    @Override
    public void run() {
        HttpURLConnection con = null;
        try {
            URL url = new URL(fileURL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            con.setRequestProperty("Range", "bytes="+startPos+"-"+endPos);
            System.out.println("bytes="+startPos+"-"+endPos);

            int responseCode = con.getResponseCode();
            System.out.println("response code is " + responseCode);
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL){
                try(InputStream ins = con.getInputStream();
                    RandomAccessFile raf = new RandomAccessFile(downloadPath, "rw")){

                    System.out.println("线程 " + threadId+" 下载区间是 "+startPos+"-"+endPos);
                    byte[] buffer = new byte[1024 * 1024];
                    int len;

                    long totalDownload=downloaded.get();
                    System.out.println("at the beginning, totalDownload is "+ totalDownload);

//                    long currentPos= startPos;
//                    raf.seek(currentPos);
                    raf.seek(startPos);
                    while((len = ins.read(buffer)) != 0){
//                        System.out.println("读取的文件长度是："+len);

                        //如何保证写时的线程安全？？？还是因为写入的位置不一样，所以没有线程安全问题？？
                        raf.write(buffer,0, len);

                        totalDownload += len;
                        downloaded.set(totalDownload);
//                        System.out.println("endpos is "+ endPos+", startpos is "+startPos+"downloaded is "+ downloaded.get());

                        if(endPos >0 && (startPos + downloaded.get()) >= endPos){
                            System.out.println(Thread.currentThread().getName() + "下载完成了....");
                            break;//下载完毕的判断对吗？？？
                        }
                    }
                  /*  System.out.println("at the end, totalDownload is "+ totalDownload);
                    System.out.println("总共下载： "+ downloaded.get());
                    System.out.println("线程 "+threadId+" 下载完成。");*/
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            con.disconnect();
        }


    }

    public long getDownloaded() {
        return downloaded.get();
    }

    public void setDownloaded(long downloaded) {
        this.downloaded.set(downloaded);
    }
}
