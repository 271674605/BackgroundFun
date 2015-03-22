package com.swpbiz.backgroundfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;


public class MainActivity extends ActionBarActivity {

    TextView tvAsyncTask;
    ProgressBar progressBarAsyncTask;

    TextView tvIntentService;
    ProgressBar intentServiceProgressBar;

    Handler handler;
    ProgressBar handlerProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAsyncTask = (TextView) findViewById(R.id.tvAsyncTask);
        progressBarAsyncTask = (ProgressBar) findViewById(R.id.progressBarAsyncTask);

        tvIntentService = (TextView) findViewById(R.id.tvIntentService);
        intentServiceProgressBar = (ProgressBar) findViewById(R.id.pbIntentService);

        handlerProgressBar = (ProgressBar) findViewById(R.id.pbHandler);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                handlerProgressBar.setProgress(msg.arg1);
            }
        };

        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Semaphore sem = new Semaphore(1);

                MyLooperThread myLooperThread = new MyLooperThread(sem);
                myLooperThread.start();

                try {
                    sem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sem.release();
                }
                tvAsyncTask.setText("AsyncTask");
                MyAsyncTask myAsyncTask = new MyAsyncTask(tvAsyncTask, progressBarAsyncTask, myLooperThread.getHandler());
                myAsyncTask.execute(30);

                tvIntentService.setText("IntentService");
                Intent i = new Intent(MainActivity.this, MyIntentService.class);
                i.putExtra("rounds", 30);
                startService(i);

                Thread myThread = new Thread(new MyThread(handler, handlerProgressBar, myLooperThread.getHandler()));
                myThread.start();

//                Intent activityIntent = new Intent(MainActivity.this, MainActivity2Activity.class);
//                startActivity(activityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(MyIntentService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(myIntentServiceReceiver, filter);
    }

    BroadcastReceiver myIntentServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", -1);
            if (progress > 0) {
                intentServiceProgressBar.setProgress(progress);
            }
            String resultValue = intent.getStringExtra("resultValue");
            if (resultValue != null) {
                tvIntentService.setText(resultValue);
            }
        }
    };

}
