package com.example.angel.sunshine.utilidades;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Angel on 28/02/2018.
 */

public class UtilidadesFecha {

    private String TAG = UtilidadesFecha.class.getSimpleName();

    public static String timestamp2String(long timestamp) {
        Date dt = new Date(timestamp * 1000);

        SimpleDateFormat sfd = new SimpleDateFormat("E MMMM yyyy", new Locale("es", "ES"));

        String fecha = sfd.format(dt);


        return fecha;
    }
}
