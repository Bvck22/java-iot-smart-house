package com.smarthome;

public class SmartApp {
    public static void main(String[] args) {
        System.out.println(">>> KHỞI ĐỘNG HỆ THỐNG SMART HOME (AI VERSION) <<<");

        // 1. Khởi tạo MQTT (Dùng lại code cũ)
        MqttManager mqttManager = new MqttManager();

        // 2. Khởi tạo Giao diện Thông minh (Thay vì DashboardUI thường)
        SmartDashboardUI smartGui = new SmartDashboardUI(mqttManager);

        // 3. Kết nối và hiển thị
        boolean isConnected = mqttManager.connect();
        smartGui.setConnectionStatus(isConnected);
        
        smartGui.setVisible(true);
    }
}