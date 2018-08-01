package com.example.angel.sunshine.utilidades;

import android.content.Context;

import com.example.angel.sunshine.data.DatosClima;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Angel on 28/02/2018.
 */

public class OpenWeatherJSON {

    private static final String TIMESTAMP = "dt";

    private static final String INFORMACION_TIEMPO_OWM = "list";
    private static final String INFORMACION_TEMPERATURA_OWM = "temp";
    private static final String TEMPERATURA_MINIMA_OWM = "temp_min";
    private static final String TEMPERATURA_MAXIMA_OWM = "temp_max";
    private static final String ICONO_OWM = "icon";



    private static final String HUMEDAD_OWM = "humidity";
    private static final String PRESION_OWM = "pressure";
    private static final String ID_WEATHER_OWM = "id";

    private static final String VIENTO_OWM = "speed";
    private static final String DIRECCION_VIENTO_OWM = "deg";

    private static final String INFORMACION_CLIMA_OWM = "weather";
    private static final String DESCRIPCION_CLIMA_OWM = "main";
    private static final String DESCRIPCION_VIENTO_OWM= "wind";
    private static final String DESCRIPCION_LLUVIA_OWM= "rain";


    private static final String CODIGO_ESTADO_OWM = "cod";
    private static final String CODIGO_MESSAGE_OWM = "message";
    private static final String LOCALIZACION_FAVORITA = "Peñacastillo, ES";

    public static String getLocalizacionFavorita() {
        return LOCALIZACION_FAVORITA;
    }


    public static ArrayList<String> tiempoSimpleString(Context context, String JSONInformacionTiempo) throws JSONException {

        ArrayList<String> datosTiempoString = new ArrayList<>();

        ArrayList<DatosClima> datosClima = obtenerDatosClimaIntervalo(JSONInformacionTiempo);

        try {
            Iterator<DatosClima> iterator = datosClima.iterator();


            while (iterator.hasNext()) {

                DatosClima datosclimaDia = iterator.next();


                String descripcionClima = UtilidadesTiempo.getWeatherIdString(context, datosclimaDia.prevision_id);

                String fechaStr = UtilidadesFecha.timestamp2String(datosclimaDia.timestamp);
                String infoTiempo = fechaStr + " " + descripcionClima + " Min: " + datosclimaDia.minTemp + " Max: " + datosclimaDia.maxTemp;

                datosTiempoString.add(infoTiempo);
            }

            return datosTiempoString;

        } catch (NullPointerException npe) {
            return null;
        }
    }


    public static ArrayList<DatosClima> obtenerDatosClimaIntervalo(String JSONInformacionTiempo) throws JSONException {


        ArrayList<DatosClima> datosTiempo = new ArrayList<>();

        JSONObject pronosticoJSON = new JSONObject(JSONInformacionTiempo);


        if (pronosticoJSON.has(CODIGO_ESTADO_OWM)) {
            int errorCode = pronosticoJSON.getInt(CODIGO_ESTADO_OWM);


            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;

                default:
                    String message = "CODE " + errorCode + ":" +pronosticoJSON.getString(CODIGO_MESSAGE_OWM);
                    throw new JSONException(message);


            }
        }

        JSONArray arrayTiempoJSON = pronosticoJSON.getJSONArray(INFORMACION_TIEMPO_OWM);


        for (int i = 0; i < arrayTiempoJSON.length(); i++) {

            JSONObject tiempoDia = arrayTiempoJSON.getJSONObject(i);
            DatosClima datosClima = obtenerDatosClimaDia(tiempoDia);
            datosTiempo.add(datosClima);

        }

        return datosTiempo;
    }


    public static DatosClima obtenerDatosClimaDia(JSONObject jsonObject) throws JSONException {


      //  JSONObject temperaturaDia = jsonObject.getJSONObject(INFORMACION_TEMPERATURA_OWM);
        JSONObject wheater = jsonObject.getJSONArray(INFORMACION_CLIMA_OWM).getJSONObject(0);
        JSONObject main = jsonObject.getJSONObject(DESCRIPCION_CLIMA_OWM);
        JSONObject wind = jsonObject.getJSONObject(DESCRIPCION_VIENTO_OWM);
        JSONObject rain = jsonObject.getJSONObject(DESCRIPCION_LLUVIA_OWM);



        //c pasar timestamp a local.
        //c verificar que la conversion de timestamp es correcta

        Long timestamp = jsonObject.getLong(TIMESTAMP);
        timestamp = UtilidadesFecha.convertUTC2LocalTimeZone(timestamp);

        Integer prevision_id = wheater.getInt(ID_WEATHER_OWM);


        Double maxTemp = main.getDouble(TEMPERATURA_MAXIMA_OWM);
        Double minTemp =  main.getDouble(TEMPERATURA_MINIMA_OWM);

        Double humedad = main.getDouble(HUMEDAD_OWM);
        Double presion = main.getDouble(PRESION_OWM);

        Double viento = wind.getDouble(VIENTO_OWM);
        Double orientacionViento = wind.getDouble(DIRECCION_VIENTO_OWM);


        String icon= wheater.getString(ICONO_OWM);
        return new DatosClima(timestamp, prevision_id, maxTemp, minTemp, humedad, presion, viento, orientacionViento, icon);

    }

}
