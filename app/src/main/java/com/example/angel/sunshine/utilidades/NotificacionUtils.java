package com.example.angel.sunshine.utilidades;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.angel.sunshine.DetallesTiempo_Activity;
import com.example.angel.sunshine.R;
import com.example.angel.sunshine.data.PronosticoContract;

import static com.example.angel.sunshine.utilidades.UtilidadesFecha.getStartOfDayTimestamp;

public class NotificacionUtils {

    private static final int NOTIFICACION_TIEMPO_ID = 100;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void notificarTiempoActual(Context context) {

        Resources resources = context.getResources();
        ContentResolver resolver = context.getContentResolver();


        Uri uriTiempo = PronosticoContract.PronosticoAcceso.CONTENT_URI;

        String[] projection = new String[]{
                PronosticoContract.PronosticoAcceso.COLUMNA_FECHA,
                PronosticoContract.PronosticoAcceso.COLUMNA_WEATHER_ID,
                PronosticoContract.PronosticoAcceso.COLUMNA_MAX_TEMP,
                PronosticoContract.PronosticoAcceso.COLUMNA_MIN_TEMP};

        long timestampInicioDia = getStartOfDayTimestamp();


        String mSelection = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + ">=?";
        String[] mSelectionArgs = new String[]{Long.toString(timestampInicioDia)};
        String sortOrder = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + " ASC";

        Cursor cursorFechas = resolver.query(uriTiempo, projection, mSelection, mSelectionArgs, sortOrder, null);


        //c introducirlo en una async task? --> no si se ejecuta como servicio


        if (cursorFechas != null) {

            cursorFechas.moveToFirst();

            int columnaID = cursorFechas.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_WEATHER_ID);
            int comumnaMaxTemp = cursorFechas.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_MAX_TEMP);
            int columnaMinTemp = cursorFechas.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_MIN_TEMP);
            int columnaFecha = cursorFechas.getColumnIndex(PronosticoContract.PronosticoAcceso.COLUMNA_FECHA);

            long fecha = cursorFechas.getLong(columnaFecha);
            int weatherId = cursorFechas.getInt(columnaID);
            String descripcion = UtilidadesTiempo.getWeatherIdString(context, weatherId);
            double max_temp = cursorFechas.getDouble(comumnaMaxTemp);
            double min_temp = cursorFechas.getDouble(columnaMinTemp);

            cursorFechas.close();

            int idIconoLarge = UtilidadesTiempo.getIDIconoVectorClimaLargo(weatherId);
            int idIconoSmall = UtilidadesTiempo.getIDIconoVectorClimaLargo(weatherId);
            Bitmap largeIcon = BitmapFactory.decodeResource(resources, idIconoLarge);

            String maxTempStr = UtilidadesTiempo.daFormatoATemperatura(context, (long) max_temp);
            String minTempStr = UtilidadesTiempo.daFormatoATemperatura(context, (long) min_temp);

            String textoNotificacion = "Prevision: " + descripcion + ". Min: " + maxTempStr + " - Max: " + minTempStr;
            String tituloNotficacion = resources.getString(R.string.tituloNotificacion);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(idIconoSmall)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(tituloNotficacion)
                            .setContentText(textoNotificacion)
                            .setAutoCancel(true);


            Uri uriTiempoHoy = PronosticoContract.PronosticoAcceso.getUriWithDate(fecha);
            Intent actividadDetalles = new Intent(context, DetallesTiempo_Activity.class);
            actividadDetalles.putExtra(Intent.EXTRA_TEXT, uriTiempoHoy.toString());

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(actividadDetalles);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);


            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICACION_TIEMPO_ID, mBuilder.build());

        }

    }


}