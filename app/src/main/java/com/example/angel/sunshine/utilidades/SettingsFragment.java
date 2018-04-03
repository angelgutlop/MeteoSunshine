package com.example.angel.sunshine.utilidades;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.angel.sunshine.R;
import com.example.angel.sunshine.data.PronosticoContract;
import com.example.angel.sunshine.sync.ForecastSyncUtilis;

/**
 * Created by Angel on 15/03/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_forecast);


        PreferenceScreen preferences = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferences.getSharedPreferences();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        //Establece los valores por defecto de cada propiedad en caso de que no existan:
        setDefaultValue(sharedPreferences, R.string.ajustes_localizacion_key, PreferenciasApp.getUbicacionPreferida());
        setDefaultValue(sharedPreferences, R.string.ajustes_seleccion_temperatura_key, PreferenciasApp.getUnidadTemperaturaPreferida());

        //Establece los summaries de cada ajuste
        setEditTextSummary(getString(R.string.ajustes_localizacion_key));
        setListPreferenceSummary(getString(R.string.ajustes_seleccion_temperatura_key));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Activity activity = getActivity();
        if (key.equals(getString(R.string.ajustes_localizacion_key))) {
            ForecastSyncUtilis.sincronizarInmediatamente(activity);

            setEditTextSummary(key);
        } else if (key.equals(getString(R.string.ajustes_seleccion_temperatura_key))) {
            activity.getContentResolver().notifyChange(PronosticoContract.PronosticoAcceso.CONTENT_URI, null);
            setListPreferenceSummary(key);
        }
    }


    private void setDefaultValue(SharedPreferences sharedPreferences, int id, Object value) {

        String key = getString(id);
        if (key == null) return;

        if (value instanceof String) {
            sharedPreferences.getString(key, (String) value);
        } else if (value instanceof Float) {
            sharedPreferences.getFloat(key, (Float) value);
        }
    }


    private void setListPreferenceSummary(String key) {

        ListPreference listPreference = (ListPreference) findPreference(key);

        if (listPreference != null) {
            String label = listPreference.getEntry().toString();
            listPreference.setSummary(label);
        }

    }


    private void setEditTextSummary(String key) {

        EditTextPreference editTextPreference = (EditTextPreference) findPreference(key);

        if (editTextPreference != null) {
            String label = editTextPreference.getText();
            editTextPreference.setSummary(label);
        }

    }

    @Override
    public void onDestroy() {

        PreferenceScreen preferences = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferences.getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

    }
}
