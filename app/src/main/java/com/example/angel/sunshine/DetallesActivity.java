package com.example.angel.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
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

import com.example.angel.sunshine.data.PronosticoContract.PronosticoAcceso;
import com.example.angel.sunshine.databinding.ActivitdadDetallesBinding;
import com.example.angel.sunshine.settings.Settings_Activity;
import com.example.angel.sunshine.utilidades.UtilidadesFecha;
import com.example.angel.sunshine.utilidades.UtilidadesTiempo;

public class DetallesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
private String pevisión_texto;
    //TextView tvPrevision;
    //Deta mainBinding;
    //Activity mainBinding;

    private ActivitdadDetallesBinding mdetallesBinding=null;

    private enum ID_LOADERS {FETCH_WEATHER_ENTRY}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activitdad_detalles);

        if(mdetallesBinding==null) mdetallesBinding = DataBindingUtil.setContentView(this, R.layout.activitdad_detalles);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setHomeButtonEnabled(true);
        } catch (NullPointerException npe) {

        }


        Uri uriDate = null;
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {

            uriDate = Uri.parse(intent.getStringExtra(Intent.EXTRA_TEXT));


            if (getSupportLoaderManager().getLoader(DetallesActivity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal()) != null) {
                getSupportLoaderManager().initLoader(DetallesActivity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal(), null, this);

            } else {
                cargarEntradaClima(uriDate);
            }
        }


    }

    private static final String URI_ID = "uri_id";

    private void cargarEntradaClima(Uri uri) {

        LoaderManager loaderManager = getSupportLoaderManager();

        //Añadir parámetros el id


        Bundle queryBundle = new Bundle();

        queryBundle.putString(URI_ID, uri.toString());

        int loaderID = DetallesActivity.ID_LOADERS.FETCH_WEATHER_ENTRY.ordinal();
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


                String mimeType = "text/Plain";
                ShareCompat.IntentBuilder shareIntent = ShareCompat.IntentBuilder.from(this);
                shareIntent.setChooserTitle("Compartir mediante");
               shareIntent.setType(mimeType).setText(pevisión_texto);

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


        data.moveToFirst();
        int idColumnaFecha = data.getColumnIndex(PronosticoAcceso.COLUMNA_FECHA);

        int idColumnaVelViento = data.getColumnIndex(PronosticoAcceso.COLUMNA_VELOCIDAD_VIENTO);
        int idColumnaOrientacionViento = data.getColumnIndex(PronosticoAcceso.COLUMNA_ORIENTACION_VIENTO);

        int idColumnaTemperatura_max = data.getColumnIndex(PronosticoAcceso.COLUMNA_MAX_TEMP);
        int idColumnaTemperatura_min = data.getColumnIndex(PronosticoAcceso.COLUMNA_MIN_TEMP);

        int idColumnaPresion = data.getColumnIndex(PronosticoAcceso.COLUMNA_PRESION);
        int idColumnaHumedad = data.getColumnIndex(PronosticoAcceso.COLUMNA_HUMEDAD);


        int idWheaterId = data.getColumnIndex(PronosticoAcceso.COLUMNA_WEATHER_ID);

        long fecha = data.getLong(idColumnaFecha);
        int weatherId = data.getInt(idWheaterId);


        Long velocidadViento = data.getLong(idColumnaVelViento);
        Long orientacionViento = data.getLong(idColumnaOrientacionViento);


        String vientoStr = UtilidadesTiempo.getFormattedWind(this, velocidadViento, orientacionViento);
String fechaConHora=UtilidadesFecha.timestamp2FullDateString(fecha);
        String descripcion=UtilidadesTiempo.getWeatherIdString(this, weatherId);

        mdetallesBinding.detallesPrincipal.iconoClima.setImageResource(UtilidadesTiempo.getIDIconoVectorClimaLargo((weatherId)));
        mdetallesBinding.detallesPrincipal.tvFechaDetalles.setText(fechaConHora);
        mdetallesBinding.detallesPrincipal.tvDescripcionDetalles.setText(descripcion);

        String maxtempStr = Long.toString(data.getLong(idColumnaTemperatura_max)) + "º";
        String mintempStr = Long.toString(data.getLong(idColumnaTemperatura_min)) + "º";
        String humedadStr = Long.toString(data.getLong(idColumnaHumedad)) + "%";
        String presionStr = Long.toString(data.getLong(idColumnaPresion)) + " hPa";

        mdetallesBinding.detallesPrincipal.tvMaxTempDetalles.setText(maxtempStr);
        mdetallesBinding.detallesPrincipal.tvMinTempDetalles.setText(mintempStr);
        mdetallesBinding.detallesExtra.tvHumedadDetalles.setText(humedadStr);

        mdetallesBinding.detallesExtra.tvPresionDetalles.setText(presionStr);
        mdetallesBinding.detallesExtra.tvVientoDetalles.setText(vientoStr);

        pevisión_texto = fechaConHora + " - Pronóstico "+descripcion.toUpperCase() +
                "\n Temperatura máxima:  " + maxtempStr +
                "\n Temperatura mínima:  " + mintempStr +
                "\n Humedad:  " + humedadStr +
                "\n Presión:  " + presionStr;


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
