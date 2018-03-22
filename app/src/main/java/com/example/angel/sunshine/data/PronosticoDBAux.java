package com.example.angel.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.angel.sunshine.data.PronosticoContract.PronosticoAcceso;

/**
 * Created by Angel on 16/03/2018.
 */

public class PronosticoDBAux extends SQLiteOpenHelper {

    public static final String NOMBRE_DATABASE = "pronostico.db";
    public static final int DATABASE_VERSION = 3;

    public PronosticoDBAux(Context context) {
        super(context, NOMBRE_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + PronosticoAcceso.NOMBRE_TABLA + " (" +

                PronosticoAcceso._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PronosticoAcceso.COLUMNA_WEATHER_ID + " INTEGER NOT NULL," +
                PronosticoAcceso.COLUMNA_FECHA + " INTEGER NOT NULL," +
                PronosticoAcceso.COLUMNA_HUMEDAD + " REAL NOT NULL," +
                PronosticoAcceso.COLUMNA_MAX_TEMP + " REAL NOT NULL," +
                PronosticoAcceso.COLUMNA_MIN_TEMP + " REAL NOT NULL," +
                PronosticoAcceso.COLUMNA_PRESION + " REAL NOT NULL," +
                PronosticoAcceso.COLUMNA_VELOCIDAD_VIENTO + " REAL NOT NULL," +
                PronosticoAcceso.COLUMNA_ORIENTACION_VIENTO + " REAL NOT NULL," +
                "UNIQUE (" + PronosticoAcceso.COLUMNA_FECHA + ") ON CONFLICT REPLACE"
                +
                ")";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PronosticoAcceso.NOMBRE_TABLA);
        onCreate(db);
    }
}
