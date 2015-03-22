package com.swpbiz.backgroundfun;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Random;

class MyThread implements Runnable {
    Handler handler;
    ProgressBar handlerProgressBar;
    Handler bgHandler;

    Random random = new Random();

    public MyThread(Handler handler, ProgressBar progressBar, Handler bgHandler) {
        this.handler = handler;
        this.handlerProgressBar = progressBar;
        this.bgHandler = bgHandler;
    }

    @Override
    public void run() {
        for (int i = 0; i < 30; i++) {
            Message message = Message.obtain();
            message.arg1 = 100 * (i+1) /30;
            final int finalI = i;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (finalI %2 == 0) {
                        handlerProgressBar.setVisibility(View.VISIBLE);
                    } else {
                        handlerProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
            handler.sendMessage(message);

            bgHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("bghandler", Thread.currentThread().getName());
                }
            });
            try {
                Thread.sleep(random.nextInt(300));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
