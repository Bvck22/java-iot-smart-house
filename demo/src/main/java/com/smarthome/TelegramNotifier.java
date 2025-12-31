package com.smarthome;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramNotifier {
    // Thay TOKEN và CHAT_ID của bạn vào đây
    private static final String API_TOKEN = "7951204215:AAGxsHkEn0KQNDONcLJIPgrBFa8qU2Gq9w4"; 
    private static final String CHAT_ID = "6215043621"; 

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
                    System.out.println("[Telegram] Đã gửi cảnh báo thành công!");
                } else {
                    System.out.println("[Telegram] Lỗi gửi tin: " + responseCode);
                }
            } catch (Exception e) {
                System.out.println("[Telegram] Lỗi kết nối: " + e.getMessage());
            }
        }).start();
    }
}