package com.smarthome;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AiDataLoader {

   
    private static final String DB_URL = "jdbc:mysql://mysql-smarthome-smarthome-java.l.aivencloud.com:18238/defaultdb?sslMode=REQUIRED";
    private static final String DB_USER = "avnadmin";
    private static final String DB_PASS = "AVNS_Sv0Uwr1QfdvMt-RAgp1";

    public List<SensorData> fetchRecentData(int limit) {
        List<SensorData> dataList = new ArrayList<>();
        // Lấy dữ liệu mới nhất giảm dần theo ID
        String sql = "SELECT temperature, humidity FROM sensor_logs ORDER BY id DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                float t = rs.getFloat("temperature");
                float h = rs.getFloat("humidity");
                dataList.add(new SensorData(t, h));
            }
        } catch (Exception e) {
            System.err.println("[AI Loader] Lỗi kết nối Aiven: " + e.getMessage());
        }
        return dataList;
    }
}