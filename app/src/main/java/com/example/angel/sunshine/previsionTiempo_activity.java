package com.example.angel.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.angel.sunshine.utilidades.ConexionForecast;
import com.example.angel.sunshine.utilidades.OpenWeatherJSON;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class previsionTiempo_activity extends AppCompatActivity {

    TextView previsionTextView;
    TextView errorMensajeTextView;
    ProgressBar barraCargaDatosTiempo;

    private String TAG = previsionTiempo_activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        previsionTextView = (TextView) findViewById(R.id.infoMeteo);
        errorMensajeTextView = findViewById(R.id.tv_mensaje_error);
        barraCargaDatosTiempo = findViewById(R.id.bp_prograsoCarga);

        cargarDatosClima();
    }


    private void cargarDatosClima() {

        new ObtenerClima().execute(OpenWeatherJSON.getLocalizacionFavorita());
    }


    private class ObtenerClima extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            if (params.length == 0) return null;
            String localizacion = params[0];

            URL url = ConexionForecast.construyeUrl(localizacion);

            try {

                String datosJsonTiempo = ConexionForecast.getResponseFromHttpUrl(url);
                return OpenWeatherJSON.tiempoSimpleString(getApplicationContext(), datosJsonTiempo);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            previsionTextView.setText("");
            errorMensajeTextView.setVisibility(View.INVISIBLE);
            barraCargaDatosTiempo.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayTiempo) {
            super.onPostExecute(arrayTiempo);

            if (arrayTiempo != null) {

                errorMensajeTextView.setVisibility(View.INVISIBLE);
                barraCargaDatosTiempo.setVisibility(View.INVISIBLE);

                for (String s : arrayTiempo) {
                    previsionTextView.append(s + "\n\n");
                }
            } else {
                errorMensajeTextView.setVisibility(View.VISIBLE);
                barraCargaDatosTiempo.setVisibility(View.INVISIBLE);
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_forecast, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.recargar_forecast:
                cargarDatosClima();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
