package com.leetcode.practice.simple;


import java.io.IOException;
import java.io.InputStream;

/**
 * @author: Linda
 * @date: 2026/1/29 15:59
 * @description:
 */
public class MainTestLinda {
    public MainTestLinda(){}
    public static void main(String[] args) {

    }

    void readFile(){
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("record_data.txt");
        if(inputStream != null){
            System.out.println("Obtained the file of the source data!");
        }
        try {
            inputStream.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
