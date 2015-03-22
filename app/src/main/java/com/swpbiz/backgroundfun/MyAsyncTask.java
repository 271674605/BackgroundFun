package com.swpbiz.backgroundfun;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MyAsyncTask extends AsyncTask<Integer, Void, String> {

    private final ProgressBar progressBar;
    Random random;
    int progress = 0;

    private TextView tvAsyncTask;

    public MyAsyncTask(TextView tvAsyncTask, ProgressBar progressBar) {
        this.tvAsyncTask = tvAsyncTask;
        this.progressBar = progressBar;
        this.progressBar.setProgress(0);
        random = new Random();
    }

    @Override
    protected String doInBackground(Integer... params) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < params[0]; i++) {
            Log.d("AsyncTask", "Started round " + i + ", progress " + progress);
            try {
                progress = 100 * (i+1) /params[0];
                publishProgress();
                Thread.sleep(random.nextInt(300));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "AsyncTask completed in " + (System.currentTimeMillis() - start);
    }

    @Override
    protected void onPostExecute(String s) {
        tvAsyncTask.setText(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        progressBar.setProgress(progress);
    }
}
