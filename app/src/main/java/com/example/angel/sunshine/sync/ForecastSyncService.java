package com.example.angel.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

public class ForecastSyncService extends IntentService {

    // Creates an IntentService.  Invoked by your subclass's constructor.

    public ForecastSyncService() {
        super("ForecastSyncService");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ForecastSyncTask.syncPronostico(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


    }
}
