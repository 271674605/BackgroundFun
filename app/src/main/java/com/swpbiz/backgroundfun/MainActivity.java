package com.swpbiz.backgroundfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
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

    private static final int MSG_SENDING_START = 999;
    private static final int MSG_COUNT_COMPLETE = 1000;
    private static final int MSG_COUNT_RESET = 1001;
    TextView tvAsyncTask;
    ProgressBar progressBarAsyncTask;

    TextView tvIntentService;
    ProgressBar intentServiceProgressBar;

    Handler handler;
    ProgressBar handlerProgressBar;

    Handler myHtHandler;
    Handler myUiHandler;
    TextView tvHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHandlerThread = (TextView) findViewById(R.id.tvHandlerThread);

        HandlerThread myHt = new HandlerThread("my handler thread");
        myHt.start();
        myHtHandler = new Handler(myHt.getLooper()) {
            int count = 0;

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_SENDING_START || msg.what == MSG_COUNT_RESET) {
                    if (msg.what == MSG_COUNT_RESET) count = 0;
                    try {
                        Thread.sleep(200);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("message", "myHtHandler handled: " + count);
                        message.setData(bundle);
                        message.what = MSG_COUNT_COMPLETE;
                        myUiHandler.sendMessage(message);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        myUiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_COUNT_COMPLETE) {
                    tvHandlerThread.setText(msg.getData().getString("message"));
                    myHtHandler.sendEmptyMessage(MSG_SENDING_START);
                } else if (msg.what == MSG_COUNT_RESET) {
                    tvHandlerThread.setText("RESET to 0");
                    myHtHandler.sendEmptyMessage(MSG_COUNT_RESET);
                }
            }
        };


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

                myUiHandler.sendEmptyMessage(MSG_COUNT_RESET);

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
