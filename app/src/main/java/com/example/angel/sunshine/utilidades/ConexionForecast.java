package com.example.angel.sunshine.utilidades;


import android.net.Uri;
import android.util.Log;

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




    private static final String TAG = ConexionForecast.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    /*

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";
    /* The number of days we want our API to return */
    private static final int numeroDias = 14;

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";


    public static URL construyeUrl(String localizacion) {

        Uri.Builder builder = Uri.parse(FORECAST_BASE_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM, localizacion);
        builder.appendQueryParameter(FORMAT_PARAM, format);
        builder.appendQueryParameter(UNITS_PARAM, units);
        builder.appendQueryParameter(DAYS_PARAM, Integer.toString(numeroDias));
        builder.build();

        try {
            URL url = new URL(builder.toString());
            Log.v(TAG,"URL creada: " + url);
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

/*
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }*/
    }

}

