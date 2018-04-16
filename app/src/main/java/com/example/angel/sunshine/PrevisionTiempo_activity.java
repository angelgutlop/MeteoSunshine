package com.example.angel.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.angel.sunshine.sync.ForecastSyncUtilis;
import com.example.angel.sunshine.utilidades.PreferenciasApp;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;


public class PrevisionTiempo_activity extends AppCompatActivity implements RecyclerAdapter.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {


    private RecyclerView rvTiempo;
    private TextView errorMensajeTextView;
    private ProgressBar barraCargaDatosTiempo;
    private RecyclerAdapter recyclerAdapter;
    private SQLObserver mContentObserver = new SQLObserver(new Handler());

    private enum ID_LOADERS {FETCH_WEATHER}


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

        recyclerAdapter = new RecyclerAdapter(this);
        recyclerAdapter.setListener(this);

        rvTiempo.setLayoutManager(layoutManager);
        rvTiempo.setAdapter(recyclerAdapter);
        rvTiempo.setHasFixedSize(true);

//c--> cargar los datos de clima en la base de datos de forma periódica.

        ForecastSyncUtilis.sincronizarInmediatamente(this);

        Uri uriClima = PronosticoContract.PronosticoAcceso.CONTENT_URI;

        // getContentResolver().registerContentObserver(uriClima, false, mContentObserver);

        // getSupportLoaderManager().initLoader(ID_LOADERS.FETCH_WEATHER.ordinal(), null, this);


        cargarDatosSQL();

        ForecastSyncUtilis.inicializarSync(context);
        //todo cargar de alguna forma los datos de la base de datos SQL en el reciler


    }

    private void cargarDatosSQL() {
        getSupportLoaderManager().initLoader(ID_LOADERS.FETCH_WEATHER.ordinal(), null, this);

    }

    @Override
    protected void onDestroy() {
        //     getContentResolver().unregisterContentObserver(mContentObserver);
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }


    //region        REGION     ***************** Gestión loader. *********************************
    //
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        ID_LOADERS idEnum = ID_LOADERS.values()[id];


        switch (idEnum) {
            case FETCH_WEATHER:

                configurarVistaEsperando();

                Uri forecast_uri = PronosticoContract.PronosticoAcceso.CONTENT_URI;
                String sortOrder = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + " ASC"; //La fecha se almacena en tiempo local

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


    Toast toast = null;
    //Esta subrutina maneja el evento de click sobre cualquiera de los elementos de la lista con el pronóstico del tiempo.


    @Override
    public void onItemClick(int id) {

        if (toast != null) toast.cancel();


        Uri uriDate = PronosticoContract.PronosticoAcceso.getUriWithDate(recyclerAdapter.getDate(id));
        Intent intent = new Intent(this, DetallesActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, uriDate.toString());
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
                getSupportLoaderManager().initLoader(ID_LOADERS.FETCH_WEATHER.ordinal(), null, this);
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

    //Todo comprobar que este observer cumple su funcion, que no es otra que cargar datos de la base de datos cuando esta informacion se actualiza
    class SQLObserver extends ContentObserver {
        public SQLObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            cargarDatosSQL();
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // do s.th.
            // depending on the handler you might be on the UI
            // thread, so be cautious!
        }
    }
}
