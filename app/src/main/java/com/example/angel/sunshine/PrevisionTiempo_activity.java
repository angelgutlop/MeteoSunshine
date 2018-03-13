package com.example.angel.sunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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


public class PrevisionTiempo_activity extends AppCompatActivity implements RecyclerAdapter.OnClickListener, LoaderManager.LoaderCallbacks<String> {


    private RecyclerView rvTiempo;
    private TextView errorMensajeTextView;
    private ProgressBar barraCargaDatosTiempo;
    private RecyclerAdapter recyclerAdapter;


    private enum ID_LOADERS {FETCH_WEATHER}



    private Context context;

    private String TAG = PrevisionTiempo_activity.class.getSimpleName();

    private String OW_LOCALIZACION_KEY = "ow_localizacion_key";


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


        if (getSupportLoaderManager().getLoader(ID_LOADERS.FETCH_WEATHER.ordinal()) != null) {
            getSupportLoaderManager().initLoader(ID_LOADERS.FETCH_WEATHER.ordinal(), null, this);

        } else {
            cargarDatosClima();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    private void cargarDatosClima() {


        Bundle queryBundle = new Bundle();
        queryBundle.putString(OW_LOCALIZACION_KEY, OpenWeatherJSON.getLocalizacionFavorita());


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
                URL url = ConexionForecast.construyeUrl(localizacion);

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
        toast = Toast.makeText(this, "Elemento " + id, Toast.LENGTH_SHORT);

        toast.show();
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

        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.recargar_forecast:
                cargarDatosClima();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
