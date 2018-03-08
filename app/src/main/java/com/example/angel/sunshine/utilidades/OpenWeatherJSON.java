package com.example.angel.sunshine.utilidades;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by Angel on 28/02/2018.
 */

public class OpenWeatherJSON {

    private static final String TIMESTAMP = "dt";

    private static final String INFORMACION_TIEMPO_OWM = "list";
    private static final String INFORMACION_TEMPERATURA_OWM = "temp";
    private static final String TEMPERATURA_MINIMA_OWM = "min";
    private static final String TEMPERATURA_MAXIMA_OWM = "max";

    private static final String INFORMACION_CLIMA_OWM = "weather";
    private static final String DESCRIPCION_CLIMA_OWM = "main";

    private static final String CODIGO_ESTADO_OWM = "cod";

    private static final String LOCALIZACION_FAVORITA = "Peñacastillo, ES";

    public static String getLocalizacionFavorita() {
        return LOCALIZACION_FAVORITA;
    }


    public static ArrayList<String> tiempoSimpleString(Context context, String JSONInformacionTiempo) throws JSONException {


        JSONObject pronosticoJSON = new JSONObject(JSONInformacionTiempo);


        if (pronosticoJSON.has(CODIGO_ESTADO_OWM)) {
            int errorCode = pronosticoJSON.getInt(CODIGO_ESTADO_OWM);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray arrayTiempoJSON = pronosticoJSON.getJSONArray(INFORMACION_TIEMPO_OWM);

        ArrayList<String> datosTiempo = new ArrayList<String>();

        for (int i = 0; i < arrayTiempoJSON.length(); i++) {

            JSONObject tiempoDia = arrayTiempoJSON.getJSONObject(i);
            JSONObject temperaturaDia = tiempoDia.getJSONObject(INFORMACION_TEMPERATURA_OWM);
            JSONObject climaDia = tiempoDia.getJSONArray(INFORMACION_CLIMA_OWM).getJSONObject(0);

            Long timestamp = tiempoDia.getLong(TIMESTAMP);
            Double maxTemp = temperaturaDia.getDouble(TEMPERATURA_MAXIMA_OWM);
            Double minTemp = temperaturaDia.getDouble(TEMPERATURA_MINIMA_OWM);

            String descripcionClima = climaDia.getString(DESCRIPCION_CLIMA_OWM);

            String fechaStr = UtilidadesFecha.timestamp2String(timestamp);
            String infoTiempo = fechaStr + " " + descripcionClima + " Min: " + minTemp + " Max: " + maxTemp;

            datosTiempo.add(infoTiempo);


        }
        return datosTiempo;
    }

}
