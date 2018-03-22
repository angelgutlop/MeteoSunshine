package com.example.angel.sunshine.utilidades;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Angel on 28/02/2018.
 */

public class UtilidadesFecha {

    private String TAG = UtilidadesFecha.class.getSimpleName();

    public static String timestamp2String(long timestamp) {

        Long timestamp_milis = TimeUnit.SECONDS.toMillis(timestamp);

        Date dt = new Date(timestamp_milis);

        SimpleDateFormat sfd = new SimpleDateFormat("E MMMM yyyy", new Locale("es", "ES"));

        String fecha = sfd.format(dt);


        return fecha;
    }

    //todo comprobar que estas dos funciones devuelven la fecha correcta


    public static long getCurrentTimestamp(DateTimeZone timeZone) {


        DateTime nowUTC = new DateTime(timeZone);
        int offset = DateTimeZone.forID(timeZone.toString()).getOffset(new DateTime());

        nowUTC = nowUTC.plusMillis(offset);

        Timestamp ts = new Timestamp(nowUTC.getMillis());

        return ts.getTime() / 1000;
    }

    public static long getCurrentUTCTimestamp() {

        return getCurrentTimestamp(DateTimeZone.UTC);
    }


    public static long getCurrentLocalTimestamp() {

        return getCurrentTimestamp(DateTimeZone.getDefault());

    }


    // todo Usar esta funcion para convertir las fechas UTC de OW a local con el fin de alamcenarlas en la base de datos SQL

    public static long convertUTC2LocalTimeZone(long utctimestamp) {


        Long timestamp_milis = TimeUnit.SECONDS.toMillis(utctimestamp);

        int offset = DateTimeZone.forID(DateTimeZone.UTC.toString()).getOffset(new DateTime());


        Timestamp tsUTC = new Timestamp(timestamp_milis);
        DateTime dtUTC = new DateTime(tsUTC);

        DateTime dtLocal = dtUTC.minusMillis(offset);

        Timestamp tsLocal = new Timestamp(dtLocal.getMillis());

        return tsLocal.getTime() / 1000;

    }

    //completado --> encontrar la forma de convertir esta cifra a milis sin perder precision

    public static String getDayOfWeek(long timestamp) {

        Long timestamp_milis = TimeUnit.SECONDS.toMillis(timestamp);
        SimpleDateFormat sfd = new SimpleDateFormat("EEEE", new Locale("es", "ES"));

        String dia = sfd.format(timestamp_milis);
        dia = dia.substring(0, 1).toUpperCase() + dia.substring(1).toLowerCase();

        return dia;
    }

}
