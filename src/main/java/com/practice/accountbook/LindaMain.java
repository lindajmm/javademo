package com.practice.accountbook;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author: Linda
 * @date: 2026/1/15 11:01
 * @description:
 */
public class LindaMain {
    //学习文件路径相关的知识
    //相对路径：src/main/
//    private static final String DATA_FILE="src/main/practice_account_data.txt";
    //相对路径：usr.dir 的父目录
//    private static final String DATA_FILE="../practice_account_data.txt";


    //在usr.dir 路径下找下面的文件
//    private static final String DATA_FILE="practice_account_data.txt";
    private static final String DATA_FILE="./practice_account_data11.txt";
    //绝对路径
//    private static final String DATA_FILE="D:\\linda\\practice_account_data.txt";
    private static final String BACKUP_FILE="src/main/resources/practice_account_data_backup.txt";

    public static void main(String[] args) throws IOException {
        File file = new File(DATA_FILE);
       /* if(file.delete()){
            System.out.println("1.file has been deleted");
        }*/
            //文件不存在时会创建文件
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                System.out.println("will create a file if it doesn't exists.");
                writer.write("this is the data of first writing");
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        String content1 = Files.readString(Paths.get(DATA_FILE));
        System.out.println("查看文件内容： "+ content1);
        System.out.println("文件长度是： "+ file.length());

        //文件已经存在
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))){
            writer.write("文件已经存在，这是第二次写入数据");
            System.out.println("the file exists, will override original content.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String content2 = Files.readString(Paths.get(DATA_FILE));
        System.out.println("查看文件内容： "+ content2);
        System.out.println("文件长度是： "+ file.length());

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))){
            writer.write("this content is appended to the previous context.");
            System.out.println("使用追加模式，在末尾追加内容.");
        }

        String content3 = Files.readString(Paths.get(DATA_FILE));
        System.out.println("查看文件内容： "+ content3);
        System.out.println("文件长度是： "+ file.length());

    }
}
