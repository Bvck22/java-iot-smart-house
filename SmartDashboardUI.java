package com.smarthome;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SmartDashboardUI extends DashboardUI {

    private JLabel lblAiStatus;
    private AiDataLoader dataLoader;
    private AiProcessor aiProcessor;
    private ScheduledExecutorService scheduler;

    public SmartDashboardUI(MqttManager mqttManager) {
        super(mqttManager); // Gọi constructor của cha để vẽ giao diện cũ
        
        // Khởi tạo các thành phần mới
        this.dataLoader = new AiDataLoader();
        this.aiProcessor = new AiProcessor();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        initAiInterface(); // Vẽ thêm UI AI
        startAiLoop();     // Bắt đầu chạy ngầm
    }

    private void initAiInterface() {
        // Tạo thanh hiển thị AI
        lblAiStatus = new JLabel("🤖 AI System: Initializing...");
        lblAiStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAiStatus.setForeground(Color.WHITE);
        lblAiStatus.setOpaque(true);
        lblAiStatus.setBackground(new Color(60, 63, 65)); // Màu xám đậm
        lblAiStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblAiStatus.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Thêm vào vị trí dưới cùng (SOUTH) của cửa sổ
        // DashboardUI cũ là JFrame, mặc định dùng BorderLayout
        this.add(lblAiStatus, BorderLayout.SOUTH);
        
        // Cập nhật lại giao diện để hiển thị thành phần mới
        this.revalidate();
    }

    private void startAiLoop() {
        // Lên lịch: Chờ 3s khởi động, sau đó lặp lại mỗi 15 giây
        scheduler.scheduleAtFixedRate(() -> {
            try {
                SwingUtilities.invokeLater(() -> lblAiStatus.setText("🤖 AI: Đang tải dữ liệu Cloud..."));

                // 1. Tải dữ liệu (Network I/O)
                List<SensorData> data = dataLoader.fetchRecentData(20);

                // 2. Phân tích
                String result = aiProcessor.analyze(data);

                // 3. Cập nhật UI
                SwingUtilities.invokeLater(() -> {
                    lblAiStatus.setText(result);
                    
                    // Đổi màu nền dựa theo mức độ nguy hiểm
                    if (result.contains("NGUY HIỂM")) {
                        lblAiStatus.setBackground(new Color(220, 53, 69)); // Đỏ
                    } else if (result.contains("CẢNH BÁO")) {
                        lblAiStatus.setBackground(new Color(255, 193, 7)); // Vàng cam
                        lblAiStatus.setForeground(Color.BLACK);
                    } else if (result.contains("DỰ BÁO")) {
                        lblAiStatus.setBackground(new Color(23, 162, 184)); // Xanh dương nhạt
                        lblAiStatus.setForeground(Color.WHITE);
                    } else {
                        lblAiStatus.setBackground(new Color(40, 167, 69)); // Xanh lá
                        lblAiStatus.setForeground(Color.WHITE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 3, 15, TimeUnit.SECONDS); // Đợi 3 giây để ổn định hệ thống. Lấy dữ liệu về mỗi 15 giây
    }

}
