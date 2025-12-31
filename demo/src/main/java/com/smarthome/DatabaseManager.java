package com.smarthome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {
    // Thông tin Aiven MySQL Cloud
    private static final String URL = "jdbc:mysql://mysql-smarthome-smarthome-java.l.aivencloud.com:18238/defaultdb?sslMode=REQUIRED";
    private static final String USER = "avnadmin"; 
    private static final String PASS = "AVNS_Sv0Uwr1QfdvMt-RAgp1"; 
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public DatabaseManager() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS sensor_logs ("
                   + "id INT AUTO_INCREMENT PRIMARY KEY, "
                   + "temperature FLOAT, "
                   + "humidity FLOAT, "
                   + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                   + ")";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("[Database Cloud] Đã kết nối MySQL và kiểm tra bảng thành công!");
            
        } catch (SQLException e) {
            System.err.println("[Database Lỗi] " + e.getMessage());
        }
    }

    public void saveSensorData(float temp, float hum) {
       executor.submit(() -> {
            String query = "INSERT INTO sensor_logs (temperature, humidity) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setFloat(1, temp);
                pstmt.setFloat(2, hum);
                pstmt.executeUpdate();
                
                // Đã comment dòng này để đỡ spam Console
                // System.out.println("[MySQL] Đã lưu: " + temp + " - " + hum);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}