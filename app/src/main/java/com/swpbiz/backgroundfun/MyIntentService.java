package com.swpbiz.backgroundfun;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Random;

public class MyIntentService extends IntentService {

    Random random;

    public static final String ACTION = "com.swpbiz.backgroundfun.MyIntentService";

    public MyIntentService() {
        super("MyIntentService");
        random = new Random();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int rounds = intent.getIntExtra("rounds", 0);
        Intent in = new Intent(ACTION);
        int progress = 0;
        long start = System.currentTimeMillis();

        for (int i = 0; i < rounds; i++) {
            try {
                Log.d("IntentService", "Started round " + i + ", progress " + progress);
                progress = 100 * (i + 1) / rounds;
                in.putExtra("progress", progress);
                LocalBroadcastManager.getInstance(this).sendBroadcast(in);
                Thread.sleep(random.nextInt(300));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        in.putExtra("resultValue", "Intent Service completed in " + (System.currentTimeMillis() - start));
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }
}

