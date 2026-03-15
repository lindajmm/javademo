package com.foundation;


/**
 * @author: Linda
 * @date: 2026/3/9 15:01
 * @description:
 */
public class ThreadDemo extends Thread{
    @Override
    public void run(){
        System.out.println("learning multiple thread, thread is "+ Thread.currentThread().getName());

    }
}
