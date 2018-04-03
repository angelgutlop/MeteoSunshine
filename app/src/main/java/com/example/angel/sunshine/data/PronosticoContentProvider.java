package com.example.angel.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Angel on 21/03/2018.
 */

public class PronosticoContentProvider extends ContentProvider {

    private PronosticoDBAux pronosticoDBAux;
    private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int WEATHER_TABLE = 100;
    private static final int WHEATER_WITH_DATE = 101;

    @Override
    public boolean onCreate() {
        pronosticoDBAux = new PronosticoDBAux(getContext());
        uriMatcher.addURI(PronosticoContract.CONTENT_AUTHORITY, PronosticoContract.PronosticoAcceso.PRONOSTICO_PATH, WEATHER_TABLE);
        uriMatcher.addURI(PronosticoContract.CONTENT_AUTHORITY, PronosticoContract.PronosticoAcceso.PRONOSTICO_PATH + "/#", WHEATER_WITH_DATE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = pronosticoDBAux.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Cursor cursor;

        switch (match) {

            case WEATHER_TABLE: {

                cursor = db.query(PronosticoContract.PronosticoAcceso.NOMBRE_TABLA, null, selection, selectionArgs, null, null, sortOrder);

                if (cursor == null) throw new SQLException("Base de datos de clima vacía");
                break;
            }
            case WHEATER_WITH_DATE: {

                String fecha = uri.getPathSegments().get(1);
                String mSelection = PronosticoContract.PronosticoAcceso.COLUMNA_FECHA + "=?";
                String[] mSelectionArgs = new String[]{fecha};

                cursor = db.query(PronosticoContract.PronosticoAcceso.NOMBRE_TABLA, null, mSelection, mSelectionArgs, null, null, sortOrder);


                break;
            }

            default: {
                throw new UnsupportedOperationException("Opcion de consulta de clima no soportada: " + uri);
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = pronosticoDBAux.getWritableDatabase();

        int match = uriMatcher.match(uri);

        switch (match) {

            case WEATHER_TABLE: {

                db.beginTransaction();


                int nRegistros = 0;

                try {
                    for (ContentValues contentValue : values) {
                        long id = db.insert(PronosticoContract.PronosticoAcceso.NOMBRE_TABLA, null, contentValue);
                        if (id > 1) nRegistros++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (nRegistros > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return nRegistros;
            }

            default: {
                throw new UnsupportedOperationException("Opcion de consulta de clima no soportada: " + uri);
            }
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("Opción de insertar no soportada. Usa bulkInsert en su lugar ");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = pronosticoDBAux.getWritableDatabase();

        int match = uriMatcher.match(uri);

        switch (match) {

            case WEATHER_TABLE: {
                int ndeleted = db.delete(PronosticoContract.PronosticoAcceso.NOMBRE_TABLA, selection, selectionArgs);
                if (ndeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ndeleted;
            }
            default: {
                throw new UnsupportedOperationException("Opcion de consulta de clima no soportada: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Opcion no implementada todavía");

    }
}
