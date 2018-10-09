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

    public static Boolean compareDateDays(Long timestampDate1, Long timestampDate2) {
        SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMdd", new Locale("es", "ES"));

        Date date1 = new Date(TimeUnit.SECONDS.toMillis(timestampDate1));
        Date date2 = new Date(TimeUnit.SECONDS.toMillis(timestampDate2));

        String dateStr1 = sfd.format(date1);
        String dateStr2 = sfd.format(date2);

        if (dateStr1.equals(dateStr2)) return true;
        else return false;
    }

    public static String timestamp2StringHour(long timestamp) {

        Long timestamp_milis = TimeUnit.SECONDS.toMillis(timestamp);
        Date dt = new Date(timestamp_milis);

        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a", new Locale("es", "ES"));
        return sfd.format(dt);
    }

    public static String timestamp2String(Long timestamp) {

        Long timestamp_milis = TimeUnit.SECONDS.toMillis(timestamp);
        Date dt = new Date(timestamp_milis);

        SimpleDateFormat sfd = new SimpleDateFormat("E d MMMM", new Locale("es", "ES"));
        return sfd.format(dt);
    }


    public static String timestamp2FullDateString(Long timestamp) {

        Long timestamp_milis = TimeUnit.SECONDS.toMillis(timestamp);
        Date dt = new Date(timestamp_milis);

        SimpleDateFormat sfd = new SimpleDateFormat("E d MMMM - HH:mm", new Locale("es", "ES"));
        return sfd.format(dt);
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


    // c Usar esta funcion para convertir las fechas UTC de OW a local con el fin de alamcenarlas en la base de datos SQL

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

    public static long getStartOfDayTimestamp() {

        DateTime dateInicioDia = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        Long milis = dateInicioDia.getMillis();
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(milis);
        Timestamp timestamp = new Timestamp(seconds);

        return timestamp.getTime();

    }


    public static long getEndOfDayTimestamp() {

        DateTime dateInicioDia = DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        Long milis = dateInicioDia.getMillis();
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(milis);
        Timestamp timestamp = new Timestamp(seconds);

        return timestamp.getTime();

    }

}
