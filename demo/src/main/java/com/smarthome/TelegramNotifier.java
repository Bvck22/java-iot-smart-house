package com.smarthome;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramNotifier {
    // Thay TOKEN và CHAT_ID của bạn vào đây
    private static final String API_TOKEN = "7951204215:AAGxsHkEn0KQNDONcLJIPgrBFa8qU2Gq9w4"; 
    private static final String CHAT_ID = "6215043621"; 
    
    // Thêm biến để quản lý thời gian gửi tin (tránh spam)
    private static long lastAlertTime = 0; 
    private static final long ALERT_COOLDOWN = 60000; // 60 giây mới gửi 1 lần

    public static void sendAlert(String message) {
        // Chạy trong luồng riêng để không làm đơ App
        new Thread(() -> {
            try {
                // Mã hóa tin nhắn (đổi khoảng trắng thành %20...)
                String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
                
                // Tạo đường dẫn gửi tin
                String urlString = "https://api.telegram.org/bot" + API_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + encodedMsg;
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("[Telegram] Warning have been sent!");
                } else {
                    System.out.println("[Telegram] Error sending message: " + responseCode);
                }
            } catch (Exception e) {
                System.out.println("[Telegram] Connection error: " + e.getMessage());
            }
        }).start();
    }

    // --- MỚI THÊM: Hàm kiểm tra ngưỡng nhiệt độ ---
    public static boolean checkAndAlertFire(float temp) {
        // Kiểm tra nếu nhiệt độ vượt ngưỡng 31.5
        if (temp > 32) {
            long currentTime = System.currentTimeMillis();
            
            // Chỉ gửi tin nhắn nếu đã qua 60 giây kể từ lần gửi trước
            if (currentTime - lastAlertTime > ALERT_COOLDOWN) {
                String msg = " Warning: Heat abnormal! : " + temp + "°C\nPlease check immediately!";
                sendAlert(msg);
                lastAlertTime = currentTime;
            }
            return true; // Trả về true để báo hiệu cho giao diện biết là đang nguy hiểm
        }
        return false; // Bình thường
    }
}