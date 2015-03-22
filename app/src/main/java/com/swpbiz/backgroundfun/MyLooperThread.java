package com.swpbiz.backgroundfun;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

public class MyLooperThread extends Thread {
    Handler handler;
    CountDownLatch latch;

    public MyLooperThread(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        if (handler != null)
            latch.countDown();
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }

}
