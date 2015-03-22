package com.swpbiz.backgroundfun;

import android.os.Handler;
import android.os.Looper;

public class MyLooperThread extends Thread {
    Handler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }

}
