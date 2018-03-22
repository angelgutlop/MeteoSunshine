package com.example.angel.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import com.example.angel.sunshine.data.PronosticoContract;
import com.example.angel.sunshine.utilidades.PreferenciasApp;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;


public class PrevisionTiempo_activity extends AppCompatActivity implements RecyclerAdapter.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {


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

        recyclerAdapter = new RecyclerAdapter(this);
        recyclerAdapter.setListener(this);

        rvTiempo.setLayoutManager(layoutManager);
        rvTiempo.setAdapter(recyclerAdapter);
        rvTiempo.setHasFixedSize(true);

//Todo--> cargar los datos de clima en la base de datos de forma periódica.
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

    //region        REGION     ***************** Gestión loader. *********************************
    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (args == null) return null;


        ID_LOADERS idEnum = ID_LOADERS.values()[id];


        switch (idEnum) {
            case FETCH_WEATHER:

                Uri forecast_uri = PronosticoContract.PronosticoAcceso.CONTENT_URI;
                String sortOrder = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + " ASC";

                //La fecha se almacena en tiempo local


                //comTODO USAR un cursor loader

                String mSlection = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + " >=?";
                String[] mSelectionArgs = new String[]{Long.toString(UtilidadesFecha.getCurrentLocalTimestamp())};


                return new CursorLoader(this, forecast_uri, null, mSlection, mSelectionArgs, sortOrder);


            default:
                return null;

        }

    }

    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Una vez terminada la consulta del clima a OW, esta rutina almancena los datos en el recicler adapter para su visualizacion y consulta

        if (cursor == null) {
            configurarVistaError();
            return;
        }
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        rvTiempo.smoothScrollToPosition(mPosition);


        recyclerAdapter.updateForecastData(cursor);

        if (cursor.getCount() > 0) configurarVistaVerLista();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getSupportLoaderManager().destroyLoader(ID_LOADERS.FETCH_WEATHER.ordinal());
        recyclerAdapter.updateForecastData(null);

    }
//endregion


/*
    @SuppressLint("StaticFieldLeak")
    private AsyncTaskLoader<Cursor> asyncFetchClima(final Bundle args) {


        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mData;


            @Override
            protected void onStartLoading() {

                super.onStartLoading();

                if (mData != null) deliverResult(mData);
                else {

                    //Reinicia la lista de clima cuando se lanza una nueva consulta
                    recyclerAdapter = new RecyclerAdapter(PrevisionTiempo_activity.this);
                    recyclerAdapter.setListener(PrevisionTiempo_activity.this);
                    rvTiempo.setAdapter(recyclerAdapter);

                    //Muesta la barra de progreso
                    configurarVistaEsperando();

                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

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
            public void deliverResult(Cursor data) {
                mData = data;
                super.deliverResult(data);
            }

        };
    }


*/


    Toast toast = null;
    //Esta subrutina maneja el evento de click sobre cualquiera de los elementos de la lista con el pronóstico del tiempo.


    @Override
    public void onItemClick(int id) {

        if (toast != null) toast.cancel();


        Uri uriDate = PronosticoContract.PronosticoAcceso.getUriWithDate(recyclerAdapter.getDate(id));
        Intent intent = new Intent(this, DetallesTiempo_Activity.class);
        intent.putExtra(Intent.EXTRA_TEXT, uriDate);
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
                    Log.d(TAG, "No se puede mostrar la ubicacion " + geoLocation.toString() + ", ya que no hay una app de mapas instalada.");
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }
}
