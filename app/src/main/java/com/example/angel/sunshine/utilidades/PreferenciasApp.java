package com.example.angel.sunshine.utilidades;

import android.content.Context;

import com.example.angel.sunshine.PrevisionTiempo_activity;
import com.example.angel.sunshine.R;

/**
 * Created by Angel on 15/03/2018.
 */

public class PreferenciasApp {

    private static String UNIDAD_TEMPERATURA_PREFERIDA = "Celsius";
    //private static String UBICACION_PREFERIDA ="Peñacastillo, ES";

    public static String getUbicacionPreferida() {
        Context context= PrevisionTiempo_activity.context;
       return context.getResources().getString(R.string.localizacion_preferida);

    }


    public static String getUnidadTemperaturaPreferida() {
        Context context= PrevisionTiempo_activity.context;
        return context.getResources().getString(R.string.centigrados_value);

    }


}
