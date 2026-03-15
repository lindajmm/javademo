package com.practice.downloaderv01;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author: Linda
 * @date: 2026/3/6 11:07
 * @description:
 */
public class Downloader01 {
    private String sourceFileURL;//e.g. http://www.baidu.com/index.html
    private String targetPath;// e.g. D:\downloads

    public Downloader01(String sourceFileURL, String targetPath) {
        this.sourceFileURL = sourceFileURL;
        this.targetPath = targetPath;
    }

    public boolean downloadFile() throws MalformedURLException {
        //get file name
        String fileName;
        int i = sourceFileURL.lastIndexOf('/');
        String substring = sourceFileURL.substring(i + 1);
        if(substring.contains("?")){
            String[] splits = substring.split("\\?");
            fileName = splits[0];
        }else{
            fileName = substring;
        }

        URL url = new URL(sourceFileURL);
        try {
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("HEAD");

            int responseCode = con.getResponseCode();
            String responseMessage = con.getResponseMessage();
            int contentLength;
            if(responseCode == 200 && responseMessage.equals("OK")){
                String headerField = con.getHeaderField("Content-Length");
                contentLength = Integer.parseInt(headerField);

                //begin to get the content of the source file
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                //获取响应数据流之后，就是要读取，然后保存到本地的文件中
                File targetDir = new File(targetPath);
                if(!targetDir.exists()){
                    targetDir.mkdirs();
                }

                File file = new File(targetPath + File.separator + fileName);
                try(InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fos = new FileOutputStream(file)){

                    byte[] buffer = new byte[1024];
                    int len;

                    while((len = inputStream.read(buffer)) != -1){
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    System.out.println("所有的数据都下载完成了！");
                }
            }else{
                System.out.println("No data in source file, please check!!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    return true;
    }

    public String getSourceFileURL() {
        return sourceFileURL;
    }

    public void setSourceFileURL(String sourceFileURL) {
        this.sourceFileURL = sourceFileURL;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
