package com.example.angel.sunshine.sync;


import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class FirebaseJobService extends JobService {
    private AsyncTask<Void, Void, Void> tarea;

    @Override
    public boolean onStartJob(final JobParameters params) {

        tarea = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                ForecastSyncTask.syncPronostico(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
                super.onPostExecute(o);
            }
        }.execute();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (tarea != null) tarea.cancel(true);
        return false;
    }
}


