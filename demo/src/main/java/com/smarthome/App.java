package com.smarthome;

public class App {
    public static void main(String[] args) {

        System.out.println("Init Smart Home MQTT...");


        MqttManager mqttManager = new MqttManager();

   
        DashboardUI gui = new DashboardUI(mqttManager);

    
        boolean isConnected = mqttManager.connect();
        
        gui.setConnectionStatus(isConnected);
    }
}