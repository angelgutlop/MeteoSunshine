package com.example.angel.sunshine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angel.sunshine.utilidades.ConexionForecast;
import com.example.angel.sunshine.utilidades.OpenWeatherJSON;
import com.example.angel.sunshine.utilidades.PreferenciasApp;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class PrevisionTiempo_activity extends AppCompatActivity implements RecyclerAdapter.OnClickListener, LoaderManager.LoaderCallbacks<String>, SharedPreferences.OnSharedPreferenceChangeListener {


    private RecyclerView rvTiempo;
    private TextView errorMensajeTextView;
    private ProgressBar barraCargaDatosTiempo;
    private RecyclerAdapter recyclerAdapter;


    private enum ID_LOADERS {FETCH_WEATHER}



    private Context context;

    private String TAG = PrevisionTiempo_activity.class.getSimpleName();

    private String OW_LOCALIZACION_KEY = "ow_localizacion_key";
    private String OW_UNIDAD_TEPERATURA_KEY = "ow_unidad_temperatura";

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (getSupportLoaderManager().getLoader(ID_LOADERS.FETCH_WEATHER.ordinal()) != null) {
            getSupportLoaderManager().initLoader(ID_LOADERS.FETCH_WEATHER.ordinal(), null, this);

        } else {
            cargarDatosClima();
        }


    }

    @Override
    protected void onDestroy() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        cargarDatosClima();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void cargarDatosClima() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String localizacion = sharedPreferences.getString(getString(R.string.ajustes_localizacion_key), PreferenciasApp.getUbicacionPreferida());

        String unidadTemperatura = sharedPreferences.getString((getString(R.string.ajustes_seleccion_temperatura_key)), PreferenciasApp.getUnidadTemperaturaPreferida());

        Bundle queryBundle = new Bundle();
        queryBundle.putString(OW_LOCALIZACION_KEY, localizacion);
        queryBundle.putString(OW_UNIDAD_TEPERATURA_KEY, unidadTemperatura);

        LoaderManager loaderManager = getSupportLoaderManager();


        int loaderID = ID_LOADERS.FETCH_WEATHER.ordinal();
        Loader<Object> climaLoader = loaderManager.getLoader(loaderID);

        if (climaLoader == null) {
            loaderManager.initLoader(loaderID, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderID, queryBundle, this);
        }


    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        if (args == null) return null;


        //Manejador de AsyncTaskLoader personalizado
        ID_LOADERS idEnum = ID_LOADERS.values()[id];


        switch (idEnum) {
            case FETCH_WEATHER:
                return asyncFetchClima(args);

            default:
                return null;

        }
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String datosJsonTiempo) {
        //Una vez tarminada la consulta del clima a OW, esta rutina almancena los datos en el recicler adapter para su visualizacion y consulta

        if (datosJsonTiempo == null) {
            configurarVistaError();
            return;
        }


        try {
            configurarVistaVerLista();
            ArrayList<String> arrayTiempo = OpenWeatherJSON.tiempoSimpleString(context, datosJsonTiempo);
            recyclerAdapter.setForecastData(arrayTiempo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        getSupportLoaderManager().destroyLoader(ID_LOADERS.FETCH_WEATHER.ordinal());

    }


    @SuppressLint("StaticFieldLeak")
    private AsyncTaskLoader<String> asyncFetchClima(final Bundle args) {


        return new AsyncTaskLoader<String>(this) {

            String mData;


            @Override
            protected void onStartLoading() {

                super.onStartLoading();

                if (mData != null) deliverResult(mData);
                else {

                    //Reinicia la lista de clima cuando se lanza una nueva consulta
                    recyclerAdapter = new RecyclerAdapter();
                    recyclerAdapter.setListener(PrevisionTiempo_activity.this);
                    rvTiempo.setAdapter(recyclerAdapter);

                    //Muesta la barra de progreso
                    configurarVistaEsperando();

                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {

                String localizacion = args.getString(OW_LOCALIZACION_KEY);
                String unidadTemperatura = args.getString(OW_UNIDAD_TEPERATURA_KEY);

                URL url = ConexionForecast.construyeUrl(localizacion, unidadTemperatura);

                try {
                    return ConexionForecast.getResponseFromHttpUrl(url);

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String data) {
                mData = data;
                super.deliverResult(data);
            }

        };
    }





    Toast toast = null;
    //Esta subrutina maneja el evento de click sobre cualquiera de los elementos de la lista con el pronóstico del tiempo.

    @Override
    public void onItemClick(int id) {

        if (toast != null) toast.cancel();

        Intent intent = new Intent(this, DetallesTiempo_Activity.class);
        intent.putExtra(Intent.EXTRA_TEXT, recyclerAdapter.getElement(id));
        startActivity(intent);

    }


    private void configurarVistaEsperando() {
        errorMensajeTextView.setVisibility(View.INVISIBLE);
        barraCargaDatosTiempo.setVisibility(View.VISIBLE);
    }


    private void configurarVistaError() {
        errorMensajeTextView.setVisibility(View.VISIBLE);
        barraCargaDatosTiempo.setVisibility(View.INVISIBLE);
    }

    private void configurarVistaVerLista() {
        errorMensajeTextView.setVisibility(View.INVISIBLE);
        barraCargaDatosTiempo.setVisibility(View.INVISIBLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_forecast, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.menu_recargar_forecast: {
                cargarDatosClima();
                break;
            }
            case R.id.menu_ajustes_forecast: {
                Intent intentPreferencias = new Intent(this, Settings_Activity.class);
                startActivity(intentPreferencias);
                break;
            }
            case R.id.menu_abrir_ubicacion: {

                String localizacion = sharedPreferences.getString(getString(R.string.ajustes_localizacion_key), PreferenciasApp.getUbicacionPreferida());

                Uri geoLocation = Uri.parse("geo:0,0?q=" + localizacion);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
