package com.example.angel.sunshine.utilidades;

/**
 * Created by Angel on 15/03/2018.
 */

public class PreferenciasApp {

    private static String UNIDAD_TEMPERATURA_PREFERIDA = "Celsius";
    private static String UBICACION_PREFERIDA = "Peñascastillo, ES";

    public static String getUbicacionPreferida() {
        return UBICACION_PREFERIDA;
    }


    public static String getUnidadTemperaturaPreferida() {
        return UNIDAD_TEMPERATURA_PREFERIDA;
    }


}
