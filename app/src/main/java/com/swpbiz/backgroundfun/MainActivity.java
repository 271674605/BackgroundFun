package com.swpbiz.backgroundfun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView tvAsyncTask;
    TextView tvIntentService;
    ProgressBar progressBarIntentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAsyncTask = (TextView) findViewById(R.id.tvAsyncTask);
        final ProgressBar progressBarAsyncTask = (ProgressBar) findViewById(R.id.progressBarAsyncTask);

        tvIntentService = (TextView) findViewById(R.id.tvIntentService);
        progressBarIntentService = (ProgressBar) findViewById(R.id.progressBarIntentService);

        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAsyncTask.setText("AsyncTask");
                MyAsyncTask myAsyncTask = new MyAsyncTask(tvAsyncTask, progressBarAsyncTask);
                myAsyncTask.execute(30);

                tvIntentService.setText("IntentService");
                Intent i = new Intent(MainActivity.this, MyIntentService.class);
                i.putExtra("rounds", 30);
                startService(i);

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
                progressBarIntentService.setProgress(progress);
            }
            String resultValue = intent.getStringExtra("resultValue");
            if (resultValue != null) {
                tvIntentService.setText(resultValue);
            }
        }
    };

}
