package com.example.angel.sunshine.sync;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import com.example.angel.sunshine.R;
import com.example.angel.sunshine.data.DatosClima;
import com.example.angel.sunshine.data.PronosticoContract;
import com.example.angel.sunshine.utilidades.ConexionForecast;
import com.example.angel.sunshine.utilidades.NotificacionUtils;
import com.example.angel.sunshine.utilidades.OpenWeatherJSON;
import com.example.angel.sunshine.utilidades.UtilidadesSQL;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ForecastSyncTask {

    Cursor mData;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void syncPronostico(Context context) {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String key_localizacion = context.getString(R.string.ajustes_localizacion_key);
        String key_unidad_temperatura = context.getString(R.string.ajustes_seleccion_temperatura_key);
        String centigrados = context.getString(R.string.centigrados_value);

        String localizacion = sharedPreferences.getString(key_localizacion, OpenWeatherJSON.getLocalizacionFavorita());
        String unidadTemperatura = sharedPreferences.getString(key_unidad_temperatura, centigrados);

        URL url = ConexionForecast.construyeUrl(localizacion, unidadTemperatura);

        try {
            String jsonResponse = ConexionForecast.getResponseFromHttpUrl(url);
            ArrayList<DatosClima> datosClima = OpenWeatherJSON.obtenerDatosClimaIntervalo(jsonResponse);

            if (datosClima != null) {
                ContentValues[] values = UtilidadesSQL.datosClima2ContentValues(datosClima);

                Uri uri = PronosticoContract.PronosticoAcceso.CONTENT_URI;
                ContentResolver contentResolver = context.getContentResolver();

                //Borra la informacion del tiempo antigua y actualiza la base de datos
                contentResolver.delete(uri, null, null);
                int insertados = contentResolver.bulkInsert(uri, values);



                String mostrarNotificacionesKey = context.getResources().getString(R.string.ajustes_mostrar_notificaciones_key);
                Boolean defaultMostrarNotificaciones = context.getResources().getBoolean(R.bool.mostrarNotificaciones);

                Boolean mostrarNotificaciones = sharedPreferences.getBoolean(mostrarNotificacionesKey, defaultMostrarNotificaciones);

                if (mostrarNotificaciones) {
                    NotificacionUtils.notificarTiempoActual(context);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
