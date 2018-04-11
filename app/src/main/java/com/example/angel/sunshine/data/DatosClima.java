package com.example.angel.sunshine.data;

import android.content.Context;

import com.example.angel.sunshine.utilidades.UtilidadesFecha;
import com.example.angel.sunshine.utilidades.UtilidadesTiempo;

public class DatosClima {

    public Long timestamp;
    public Integer prevision_id;
    public Double maxTemp;
    public Double minTemp;
    public Double humedad;
    public Double presion;
    public Double viento;
    public Double orientacionViento;


    public DatosClima(Long timestamp, Integer prevision_id, Double maxTemp, Double minTemp, Double humedad, Double presion, Double viento, Double orientacionViento) {
        this.timestamp = timestamp;
        this.prevision_id = prevision_id;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.humedad = humedad;
        this.presion = presion;
        this.viento = viento;
        this.orientacionViento = orientacionViento;
    }

    public String getFecha() {
        return UtilidadesFecha.timestamp2String(this.timestamp);
    }

    public String getDescipcion(Context context) {
        return UtilidadesTiempo.getWeatherIdString(context, this.prevision_id);
    }

    public String getMaxTempStr(Context context) {
        return Math.round(this.maxTemp) + "º";
    }

    public String getMinTempStr(Context context) {
        return Math.round(this.minTemp) + "º";
    }

    public int getIconoClima() {
        return UtilidadesTiempo.getIDIconoVectorClimaLargo(this.prevision_id);
    }


}
