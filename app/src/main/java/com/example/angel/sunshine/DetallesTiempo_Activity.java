package com.example.angel.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.angel.sunshine.data.PronosticoContract.PronosticoAcceso;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;
import com.example.angel.sunshine.utilidades.UtilidadesTiempo;

public class DetallesTiempo_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView tvPrevision;

    private enum ID_LOADERS {FETCH_WEATHER_ENTRY}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_tiempo);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        // tod
        tvPrevision = findViewById(R.id.tv_detalle_pronostico_tiempo);


        Uri uriDate = null;
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {

            uriDate = Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT));

        }

        if (getSupportLoaderManager().getLoader(DetallesTiempo_Activity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal()) != null) {
            getSupportLoaderManager().initLoader(DetallesTiempo_Activity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal(), null, this);

        } else {
            cargarEntradaClima(uriDate);
        }

    }

    private static final String URI_ID = "uri_id";

    private void cargarEntradaClima(Uri uri) {

        LoaderManager loaderManager = getSupportLoaderManager();

        //Añadir parámetros el id


        Bundle queryBundle = new Bundle();

        queryBundle.putString(URI_ID, uri.toString());

        int loaderID = DetallesTiempo_Activity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal();
        Loader<Object> climaLoader = loaderManager.getLoader(loaderID);

        if (climaLoader == null) {
            loaderManager.initLoader(loaderID, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderID, queryBundle, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalles, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.accion_compartir_detalles: {

                String texto = tvPrevision.getText().toString();
                String mimeType = "text/Plain";
                ShareCompat.IntentBuilder shareIntent = ShareCompat.IntentBuilder.from(this);
                shareIntent.setChooserTitle("Compartir mediante");
                shareIntent.setType(mimeType).setText(texto);

                Intent intent = shareIntent.getIntent();

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                break;
            }
            case R.id.accion_ajustes_detalles: {
                Intent intentPreferencias = new Intent(this, Settings_Activity.class);
                startActivity(intentPreferencias);
                break;
            }
            case android.R.id.home: {
                //  NavUtils.navigateUpFromSameTask(this);

                Intent upIntent = NavUtils.getParentActivityIntent(this);
             /*   if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.*/
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
                /*} else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }*/
                return true;

            }


        }
        return super.onOptionsItemSelected(item);
    }

    //region REGION  ****** GESTION DEL LOADER ********
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (args == null) return null;

        Uri foracast_uri;

        if (args.containsKey(URI_ID)) {
            foracast_uri = Uri.parse(args.getString(URI_ID));
        } else {
            throw new IllegalArgumentException("No se ha declarado el identificador del tiempo antes de acceder a la base de datos");
        }


        ID_LOADERS id_loader = ID_LOADERS.values()[id];


        switch (id_loader) {
            case FETCH_WEATHER_ENTRY: {

                return new CursorLoader(this, foracast_uri, null, null, null, null);


            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //co rellenar una vez acabe el load del contenido y actualizar vistas
        //co Añadir varis vistas para visualizar el texto de forma adecuada
        //todo Recabar informacion de los ids para que no se carguen cada vez que se abra la actividad

        data.moveToFirst();
        int idColumnaFecha = data.getColumnIndex(PronosticoAcceso.COLUMNA_FECHA);

        int idColumnaVelViento = data.getColumnIndex(PronosticoAcceso.COLUMNA_VELOCIDAD_VIENTO);
        int idColumnaOrientacionViento = data.getColumnIndex(PronosticoAcceso.COLUMNA_ORIENTACION_VIENTO);

        int idColumnaTemperatura_max = data.getColumnIndex(PronosticoAcceso.COLUMNA_MAX_TEMP);
        int idColumnaTemperatura_min = data.getColumnIndex(PronosticoAcceso.COLUMNA_MIN_TEMP);

        int idColumnaPresion = data.getColumnIndex(PronosticoAcceso.COLUMNA_PRESION);
        int idColumnaHumedad = data.getColumnIndex(PronosticoAcceso.COLUMNA_HUMEDAD);


        int id_weather_String = data.getColumnIndex(PronosticoAcceso.COLUMNA_WEATHER_ID);

        String descripcion = UtilidadesTiempo.getWeatherIdString(this, data.getInt(id_weather_String));

        TextView tvFecha = findViewById(R.id.tv_fecha_detalles);
        TextView tvVelViento = findViewById(R.id.tv_velocidad_viento_detalles);
        TextView tvOrViento = findViewById(R.id.tv_orientancion_viento_detalles);
        TextView tvTempMax = findViewById(R.id.tv_max_temp_detalles);
        TextView tvTempMin = findViewById(R.id.tv_min_temp_detalles);
        TextView tvPresion = findViewById(R.id.tv_presion_detalles);
        TextView tvHumedad = findViewById(R.id.tv_humedad_detalles);
        TextView tvDescricion = findViewById(R.id.tv_descripcion_detalles);

        //comp convertir esta fecha a numeros leibles por el usuario
        long fecha = data.getLong(idColumnaFecha);
        tvFecha.setText(UtilidadesFecha.timestamp2String(fecha));

        tvVelViento.setText(Long.toString(data.getLong(idColumnaVelViento)));
        tvOrViento.setText(Long.toString(data.getLong(idColumnaOrientacionViento)));

        tvTempMax.setText(Long.toString(data.getLong(idColumnaTemperatura_max)));

        tvTempMin.setText(Long.toString(data.getLong(idColumnaTemperatura_min)));

        tvPresion.setText(Long.toString(data.getLong(idColumnaPresion)));

        tvHumedad.setText(Long.toString(data.getLong(idColumnaHumedad)));

        tvDescricion.setText(descripcion);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
