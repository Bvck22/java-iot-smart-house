package com.smarthome;

import java.util.List;

public class AiProcessor {

    public String analyze(List<SensorData> history) {
        if (history == null || history.isEmpty()) {
            return "AI: Đang chờ dữ liệu từ Cloud...";
        }

        float totalTemp = 0;
        float maxTemp = -100;
        float totalHum = 0;

        for (SensorData d : history) {
            totalTemp += d.getTemp();
            totalHum += d.getHum();
            if (d.getTemp() > maxTemp) maxTemp = d.getTemp();
        }
        
        float avgTemp = totalTemp / history.size();
        float avgHum = totalHum / history.size();

        // --- LOGIC DỰ ĐOÁN (Rule-Based) ---
        if (maxTemp > 45.0) {
            return "🔥 NGUY HIỂM: Nhiệt độ quá cao (" + maxTemp + "°C). Cảnh báo cháy!";
        } else if (avgTemp > 35.0) {
            return "⚠️ CẢNH BÁO: Trời rất nóng (" + String.format("%.1f", avgTemp) + "°C).";
        } else if (avgHum > 90.0) {
            return "🌧️ DỰ BÁO: Độ ẩm bão hòa. Trời sắp mưa.";
        } else if (avgHum < 30.0) {
            return "🌵 KHÔ HANH: Độ ẩm quá thấp (" + String.format("%.1f", avgHum) + "%).";
        } else {
            return "✅ ỔN ĐỊNH: Hệ thống hoạt động bình thường.";
        }
    }
}