package com.smarthome;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.nio.charset.StandardCharsets;

public class MqttManager {
    // 1. Cấu hình Server MQTT
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "JavaApp_SmartHome_" + System.currentTimeMillis();
    
    // 2. Các TOPIC 
    // App gửi lệnh vào kênh này -> ESP32 lắng nghe
    public static final String TOPIC_CONTROL = "smarthome/k22/control"; 
    // ESP32 gửi data vào kênh này -> App lắng nghe
    public static final String TOPIC_DATA = "smarthome/k22/sensor";     

    private MqttClient client;
    private DataListener listener; 

    public boolean connect() {
        try {
            // Tạo Client và lưu bộ nhớ tạm (RAM)
            client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10); 

            System.out.println("Connecting to Broker: " + BROKER);
            client.connect(options);
            
            if (client.isConnected()) {
                System.out.println(">> MQTT Successfully Connected!");
                startSubscribing(); 
                return true;
            }
        } catch (MqttException e) {
            System.out.println("MQTT Error: " + e.getMessage());
        }
        return false;
    }

    // Hàm gửi lệnh điều khiển
    public void publish(String message) {
        if (client != null && client.isConnected()) {
            try {
                MqttMessage msg = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
                msg.setQos(0); 
                client.publish(TOPIC_CONTROL, msg);
                System.out.println("[MQTT Send]: " + message + " -> " + TOPIC_CONTROL);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm đăng ký lắng nghe dữ liệu từ ESP32
    private void startSubscribing() {
        try {
            client.subscribe(TOPIC_DATA, (topic, msg) -> {
                String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                
                
                if (listener != null) {
                    listener.onDataReceived(payload);
                }
            });
            System.out.println(">> Subscribing Topic: " + TOPIC_DATA);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Đăng ký listener
    public void setDataListener(DataListener listener) {
        this.listener = listener;
    }
}