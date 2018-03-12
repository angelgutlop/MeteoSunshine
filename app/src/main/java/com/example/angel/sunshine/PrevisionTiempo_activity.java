package com.example.angel.sunshine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angel.sunshine.utilidades.ConexionForecast;
import com.example.angel.sunshine.utilidades.OpenWeatherJSON;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class PrevisionTiempo_activity extends AppCompatActivity implements RecyclerAdapter.OnClickListener {


    private RecyclerView rvTiempo;
    private TextView errorMensajeTextView;
    private ProgressBar barraCargaDatosTiempo;
    private RecyclerAdapter recyclerAdapter;

    private Context context;

    private String TAG = PrevisionTiempo_activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        rvTiempo = findViewById(R.id.rv_infoMeteo);
        errorMensajeTextView = findViewById(R.id.tv_mensaje_error);
        barraCargaDatosTiempo = findViewById(R.id.bp_prograsoCarga);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerAdapter = new RecyclerAdapter();

        recyclerAdapter.setListener(this);

        rvTiempo.setLayoutManager(layoutManager);
        rvTiempo.setAdapter(recyclerAdapter);
        rvTiempo.setHasFixedSize(true);

        cargarDatosClima();
    }



    private void cargarDatosClima() {

        new ObtenerClima().execute(OpenWeatherJSON.getLocalizacionFavorita());
    }

    Toast toast = null;
    //Esta subrutina maneja el evento de click sobre cualquiera de los elementos de la lista con el pronóstico del tiempo.

    @Override
    public void onItemClick(int id) {

        if (toast != null) toast.cancel();

        Intent intent = new Intent(this, DetallesTiempo_Activity.class);
        intent.putExtra(Intent.EXTRA_TEXT, recyclerAdapter.getElement(id));
        startActivity(intent);
        toast = Toast.makeText(this, "Elemento " + id, Toast.LENGTH_SHORT);

        toast.show();
    }


    private class ObtenerClima extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            if (params.length == 0) return null;
            String localizacion = params[0];

            URL url = ConexionForecast.construyeUrl(localizacion);

            try {

                String datosJsonTiempo = ConexionForecast.getResponseFromHttpUrl(url);
                return OpenWeatherJSON.tiempoSimpleString(context, datosJsonTiempo);

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

            errorMensajeTextView.setVisibility(View.INVISIBLE);
            barraCargaDatosTiempo.setVisibility(View.VISIBLE);

            recyclerAdapter = new RecyclerAdapter();
            recyclerAdapter.setListener(PrevisionTiempo_activity.this);
            rvTiempo.setAdapter(recyclerAdapter);
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayTiempo) {
            super.onPostExecute(arrayTiempo);

            if (arrayTiempo != null) {

                errorMensajeTextView.setVisibility(View.INVISIBLE);
                barraCargaDatosTiempo.setVisibility(View.INVISIBLE);


                recyclerAdapter.setForecastData(arrayTiempo);

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
