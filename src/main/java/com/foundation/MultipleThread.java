package com.foundation;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Linda
 * @date: 2026/3/3 16:11
 * @description:
 */
public class MultipleThread {
    public static void main(String[] args) {

        ThreadDemo threadDemo = new ThreadDemo();
        threadDemo.start();

        ThreadDemo threadDemo1 = new ThreadDemo();
        threadDemo1.start();

        // 1. 创建固定大小的线程池（核心数5）
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // 2. 提交10个任务，线程池复用5个线程执行
        for (int i = 0; i < 10; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程执行：" + Thread.currentThread().getName());

                }
            });
        }

        // 3. 关闭线程池
        executor.shutdown();

    }
}
