package com.example.angel.sunshine.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.angel.sunshine.data.PronosticoContract;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class ForecastSyncUtilis {
    //todo establecer un intervalo adecuado y comprobar si la info ha cambiado
    private static final int SYNC_INTERVAL_HOURS = 5;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 6;

    static boolean inizializado = false;

    public static void sincronizarInmediatamente(Context context) {
        Intent service = new Intent(context, ForecastSyncService.class);
        context.startService(service);
    }


    public static void inicializarSync(@NonNull final Context context) {

        if (inizializado) return;

        inicializarServicioConsultaTiempo(context);

        AsyncTask asyncTask = new AsyncTask() {

            Cursor cursor;

            @Override
            protected Object doInBackground(Object[] objects) {

                String[] projectionColumns = {PronosticoContract.PronosticoAcceso._ID};
                String seleccion = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + " >= " + UtilidadesFecha.getCurrentLocalTimestamp();

                Uri uri = PronosticoContract.PronosticoAcceso.CONTENT_URI;
                ContentResolver resolver = context.getContentResolver();
                cursor = resolver.query(uri, projectionColumns, seleccion, null, null);


                if (cursor != null) {
                    if (cursor.getCount() > 0) inizializado = true;
                    cursor.close();
                }


                if (!inizializado) sincronizarInmediatamente(context);


                return null;
            }


        };

        asyncTask.execute();

    }

    private static String SERVICE_SYNC_TAG = "servicio_tag_firebase";

    private static void inicializarServicioConsultaTiempo(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));



        /* Create the Job to periodically sync Sunshine */
        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(FirebaseJobService.class)
                .setTag(SERVICE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)

                .setLifetime(Lifetime.FOREVER)

                .setRecurring(true)

                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))

                .setReplaceCurrent(true)

                .build();


        dispatcher.schedule(syncSunshineJob);
    }
}
