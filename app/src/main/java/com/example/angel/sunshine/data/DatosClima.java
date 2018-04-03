package com.example.angel.sunshine.data;

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
}
