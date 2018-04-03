package com.example.angel.sunshine.utilidades;

import android.content.ContentValues;

import com.example.angel.sunshine.data.DatosClima;
import com.example.angel.sunshine.data.PronosticoContract.PronosticoAcceso;

import java.util.ArrayList;


public class UtilidadesSQL {

    public static ContentValues[] datosClima2ContentValues(ArrayList<DatosClima> datosClimaArrayList) {

        ContentValues[] contentValues = new ContentValues[datosClimaArrayList.size()];

        for (int i = 0; i < datosClimaArrayList.size(); i++) {
            contentValues[i] = datosClima2ContentValues(datosClimaArrayList.get(i));
        }


        return contentValues;
    }

    public static ContentValues datosClima2ContentValues(DatosClima datosClima) {
        ContentValues values = new ContentValues();
        values.put(PronosticoAcceso.COLUMNA_FECHA, datosClima.timestamp);
        values.put(PronosticoAcceso.COLUMNA_HUMEDAD, datosClima.humedad);
        values.put(PronosticoAcceso.COLUMNA_MAX_TEMP, datosClima.maxTemp);
        values.put(PronosticoAcceso.COLUMNA_MIN_TEMP, datosClima.minTemp);
        values.put(PronosticoAcceso.COLUMNA_ORIENTACION_VIENTO, datosClima.orientacionViento);
        values.put(PronosticoAcceso.COLUMNA_VELOCIDAD_VIENTO, datosClima.viento);
        values.put(PronosticoAcceso.COLUMNA_PRESION, datosClima.presion);
        values.put(PronosticoAcceso.COLUMNA_WEATHER_ID, datosClima.prevision_id);

        return values;
    }
}
