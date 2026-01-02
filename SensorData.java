package com.smarthome;

public class SensorData {
    private float temp;
    private float hum;

    public SensorData(float temp, float hum) {
        this.temp = temp;
        this.hum = hum;
    }

    public float getTemp() { return temp; }
    public float getHum() { return hum; }
}