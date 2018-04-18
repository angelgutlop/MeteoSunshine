package com.example.angel.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Angel on 16/03/2018.
 */

public class PronosticoContract {

    public static final String CONTENT_AUTHORITY = "com.example.angel.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class PronosticoAcceso implements BaseColumns {

        public static final String NOMBRE_TABLA = "pronostico";

        public static final String COLUMNA_FECHA = "fecha";
        public static final String COLUMNA_WEATHER_ID = "pronostico_id";

        public static final String COLUMNA_MIN_TEMP = "min_temp";
        public static final String COLUMNA_MAX_TEMP = "max_tamp";

        public static final String COLUMNA_HUMEDAD = "humedad";

        public static final String COLUMNA_PRESION = "presion";


        public static final String COLUMNA_VELOCIDAD_VIENTO = "viento";
        public static final String COLUMNA_ORIENTACION_VIENTO = "orientacion_viento";


        public static final String PRONOSTICO_PATH = "tiempo";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PRONOSTICO_PATH).build();


        public static Uri getUriWithDate(long date) {


            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();


        }
    }


}
