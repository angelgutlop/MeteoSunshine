package com.example.angel.sunshine.utilidades;


import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Angel on 27/02/2018.
 */

public class ConexionForecast {

    private  static final String API_KEY="4c5b37cc37318fb93f86c864768e588a";
    private static final String DAILY_WEATHER_URL= "https://api.openweathermap.org/data/2.5/forecast/daily"; //Disponible bajo pago
    private static final String FIVE_DAYS_3H_WEATHER_URL= "https://api.openweathermap.org/data/2.5/forecast/";


    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";


    private static final String FORECAST_BASE_URL = FIVE_DAYS_3H_WEATHER_URL;


    private static final String TAG = ConexionForecast.class.getSimpleName();

    /*

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final int numeroDias = 14;

    private final static String QUERY_PARAM = "q";
    private final static String LAT_PARAM = "lat";
    private final static String LON_PARAM = "lon";
    private final static String FORMAT_PARAM = "mode";
    private final static String UNITS_PARAM = "units";
    private final static String DAYS_PARAM = "cnt";
    private final static String API_PARAM = "appid";


    public static URL construyeUrl(String localizacion, String unidadTemperatura) {

        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM, localizacion);
        builder.appendQueryParameter(FORMAT_PARAM, format);
        builder.appendQueryParameter(UNITS_PARAM, unidadTemperatura);
  //      builder.appendQueryParameter(DAYS_PARAM, Integer.toString(numeroDias));
      builder.appendQueryParameter(API_PARAM, API_KEY);

        builder.build();

        try {
            URL url = new URL(builder.toString());
            Log.v(TAG, "URL creada: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url)
                .build();
       Response response = client.newCall(request).execute();
    return response.body().string();




    }

}


