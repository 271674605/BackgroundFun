package com.swpbiz.backgroundfun;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class MyLooperThread extends Thread {
    Handler handler;
    Semaphore latch;

    public MyLooperThread(Semaphore latch) {
        try {
            latch.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.latch = latch;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        if (handler != null)
            latch.release();
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }

}
