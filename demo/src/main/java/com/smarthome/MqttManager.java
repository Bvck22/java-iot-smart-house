package com.smarthome;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.nio.charset.StandardCharsets;

public class MqttManager {
    // 1. Cấu hình Server (Dùng server public miễn phí của HiveMQ để test)
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "JavaApp_SmartHome_" + System.currentTimeMillis();
    
    // 2. Các TOPIC (Kênh phát thanh)
    // Java gửi lệnh vào kênh này -> ESP32 lắng nghe
    public static final String TOPIC_CONTROL = "smarthome/k22/control"; 
    // ESP32 gửi nhiệt độ vào kênh này -> Java lắng nghe
    public static final String TOPIC_DATA = "smarthome/k22/sensor";     

    private MqttClient client;
    private DataListener listener; // Vẫn dùng lại Interface cũ của bạn

    public boolean connect() {
        try {
            // Tạo Client và lưu bộ nhớ tạm (RAM)
            client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10); // 10 giây timeout

            System.out.println("Đang kết nối tới Broker: " + BROKER);
            client.connect(options);
            
            if (client.isConnected()) {
                System.out.println(">> Đã kết nối MQTT thành công!");
                startSubscribing(); // Kết nối xong là đăng ký nghe ngay
                return true;
            }
        } catch (MqttException e) {
            System.out.println("Lỗi kết nối MQTT: " + e.getMessage());
        }
        return false;
    }

    // Hàm gửi lệnh (Thay thế sendData của Serial)
    public void publish(String message) {
        if (client != null && client.isConnected()) {
            try {
                MqttMessage msg = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
                msg.setQos(0); // Qos 0: Gửi 1 lần, không cần xác nhận (nhanh)
                client.publish(TOPIC_CONTROL, msg);
                System.out.println("[MQTT Gửi]: " + message + " -> " + TOPIC_CONTROL);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm đăng ký lắng nghe dữ liệu từ cảm biến
    private void startSubscribing() {
        try {
            client.subscribe(TOPIC_DATA, (topic, msg) -> {
                String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                // System.out.println("Nhận từ " + topic + ": " + payload);
                
                // Báo về cho giao diện (DashboardUI)
                if (listener != null) {
                    listener.onDataReceived(payload);
                }
            });
            System.out.println(">> Đang lắng nghe Topic: " + TOPIC_DATA);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Đăng ký người nghe (Observer)
    public void setDataListener(DataListener listener) {
        this.listener = listener;
    }
}