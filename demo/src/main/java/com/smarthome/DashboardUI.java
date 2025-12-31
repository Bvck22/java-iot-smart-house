package com.smarthome;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DashboardUI extends JFrame implements DataListener {
    
    private JLabel lblTemp;
    private JLabel lblStatus;
    
    private DatabaseManager dbManager = new DatabaseManager();
    private MqttManager mqttManager;

    private XYSeries seriesTemp;
    private int timeSecond = 0;

    private long lastAlertTime = 0; 
    private static final long ALERT_COOLDOWN = 60000;

    public DashboardUI(MqttManager manager) { 
        this.mqttManager = manager;
        this.mqttManager.setDataListener(this);
        setupUI();
    }

    private void setupUI() {
        setTitle("Hệ Thống IoT Smart Home - MQTT Cloud Control");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Trung tâm điều khiển", createDashboardPanel());
        tabbedPane.addTab("Biểu đồ nhiệt độ", createChartPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createDashboardPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());

        JPanel pnlDisplay = new JPanel(new GridLayout(2, 1));
        pnlDisplay.setBackground(new Color(240, 248, 255));
        
        lblTemp = new JLabel("Nhiệt độ: -- °C | Độ ẩm: -- %", SwingConstants.CENTER);
        lblTemp.setFont(new Font("Arial", Font.BOLD, 28));
        lblTemp.setForeground(new Color(0, 102, 204));
        
        lblStatus = new JLabel("Trạng thái: Đang kết nối MQTT...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 14));
        
        pnlDisplay.add(lblTemp);
        pnlDisplay.add(lblStatus);
        pnlMain.add(pnlDisplay, BorderLayout.NORTH);

        JPanel pnlControls = new JPanel(new GridLayout(1, 3, 20, 20));
        pnlControls.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlControls.add(createDevicePanel("ĐÈN PHÒNG KHÁCH", "1", "0", new Color(255, 255, 204)));
        pnlControls.add(createDevicePanel("QUẠT TRẦN", "2", "3", new Color(204, 255, 204)));
        pnlControls.add(createDevicePanel("ĐIỀU HÒA", "4", "5", new Color(204, 229, 255)));

        pnlMain.add(pnlControls, BorderLayout.CENTER);
        return pnlMain;
    }

    private JPanel createDevicePanel(String deviceName, String onCmd, String offCmd, Color bgColor) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), deviceName, TitledBorder.CENTER, TitledBorder.TOP
        ));
        panel.setBackground(bgColor);

        JLabel lblIcon = new JLabel(deviceName, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblIcon);

        JButton btnOn = new JButton("BẬT " + deviceName);
        btnOn.setBackground(Color.WHITE);
        btnOn.setForeground(new Color(0, 150, 0));
        btnOn.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnOn.addActionListener(e -> {
            mqttManager.publish(onCmd);
            JOptionPane.showMessageDialog(this, "Đã gửi lệnh BẬT " + deviceName);
        });

        JButton btnOff = new JButton("TẮT " + deviceName);
        btnOff.setBackground(Color.WHITE);
        btnOff.setForeground(Color.RED);
        btnOff.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnOff.addActionListener(e -> {
            mqttManager.publish(offCmd);
            JOptionPane.showMessageDialog(this, "Đã gửi lệnh TẮT " + deviceName);
        });

        panel.add(btnOn);
        panel.add(btnOff);
        return panel;
    }

    private JPanel createChartPanel() {
        seriesTemp = new XYSeries("Nhiệt độ (°C)");
        XYSeriesCollection dataset = new XYSeriesCollection(seriesTemp);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Diễn biến nhiệt độ", "Thời gian", "Nhiệt độ",
                dataset, PlotOrientation.VERTICAL, true, true, false
        );
     
        // Cấu hình để hiện chấm tròn (Fix lỗi không vẽ khi ít dữ liệu)
        org.jfree.chart.plot.XYPlot plot = chart.getXYPlot();
           org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        
        // 2. Tắt chế độ tự động bao gồm số 0 (nếu không nó sẽ luôn cố hiện số 0)
        rangeAxis.setAutoRangeIncludesZero(false);
        
        // 3. Cài đặt cứng khoảng từ 26 đến 32
        rangeAxis.setRange(27.0, 31.0);
        org.jfree.chart.renderer.xy.XYLineAndShapeRenderer renderer = new org.jfree.chart.renderer.xy.XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true); 
        renderer.setSeriesLinesVisible(0, true);
        plot.setRenderer(renderer);
        
        return new ChartPanel(chart);
    }

    public void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            lblStatus.setText("Trạng thái: Online (Đã nối tới MQTT Broker)");
            lblStatus.setForeground(new Color(0, 150, 0));
        } else {
            lblStatus.setText("Trạng thái: Mất kết nối MQTT!");
            lblStatus.setForeground(Color.RED);
        }
    }

    @Override
    public void onDataReceived(String data) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (data.contains(";")) {
                    String[] parts = data.split(";");
                    float temp = Float.parseFloat(parts[0].trim());
                    float hum = Float.parseFloat(parts[1].trim());

                    lblTemp.setText("Nhiệt độ: " + temp + " °C | Độ ẩm: " + hum + "%");

                    dbManager.saveSensorData(temp, hum);

                    timeSecond += 10;
                    seriesTemp.add(timeSecond, temp);

                    // --- SỬA ĐOẠN NÀY ---
                    // Gọi sang TelegramNotifier để kiểm tra logic 31.5 độ
                    boolean isDangerous = TelegramNotifier.checkAndAlertFire(temp);

                    if (isDangerous) {
                        lblStatus.setText("🔥 CẢNH BÁO: NHÀ CHÁY! 🔥");
                        lblStatus.setForeground(Color.RED);
                    } else {
                        lblStatus.setText("Trạng thái: Online (Bình thường)");
                        lblStatus.setForeground(new Color(0, 150, 0));
                    }
                    // --------------------
                }
            } catch (Exception e) {
                System.err.println("Lỗi xử lý data: " + e.getMessage());
            }
        });
    }
}