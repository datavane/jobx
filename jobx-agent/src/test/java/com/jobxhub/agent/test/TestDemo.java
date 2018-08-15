package com.jobxhub.agent.test;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TestDemo {

    @Test
    public void test() throws IOException {

        final Boolean lock = false;

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    for (;;) {
                        System.out.println("1>>>>");
                    }
                }
            }
        });

        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i=0;i<10000;i++) {
                    System.out.println("2>>>>"+i);

                    if (i==1000) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (i==2000) {
                        thread.notify();
                    }
                }

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                thread.start();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                thread1.start();
            }
        }).start();


        System.in.read();
    }

}
