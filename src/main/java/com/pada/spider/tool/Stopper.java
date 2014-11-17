package com.pada.spider.tool;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by eqyun on 2014/11/4.
 */
public class Stopper extends Thread implements Observer {
    private String key;

    public Stopper(String key) {
        this.key = key;
    }

    public void run() {
        synchronized (this) {
            try {

                this.wait();//15后自动唤醒
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void wake() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void update(Observable o, Object arg) {

        String key = (String) arg;

        if(key.equals(this.key)){
            wake();
        }
    }
}
