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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PiePlot; // --- MỚI: Dùng cho biểu đồ tròn ---
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset; // --- MỚI: Dataset cho biểu đồ tròn ---
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DashboardUI extends JFrame implements DataListener {
    
    private JLabel lblTemp;
    private JLabel lblStatus;
    
    private DatabaseManager dbManager = new DatabaseManager();
    private MqttManager mqttManager;

    private XYSeries seriesTemp;
    private XYSeries seriesHum;
    
    // --- MỚI: Dataset cho biểu đồ tròn mưa ---
    private DefaultPieDataset rainDataset;
    
    private int timeSecond = 0;

    private long lastAlertTime = 0; 
    private static final long ALERT_COOLDOWN = 60000;

    public DashboardUI(MqttManager manager) { 
        this.mqttManager = manager;
        this.mqttManager.setDataListener(this);
        setupUI();
    }

    private void setupUI() {
        setTitle("IoT Smart Home System Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Control Center", createDashboardPanel());
        tabbedPane.addTab("Temp & Hum Chart", createChartPanel());
        // --- MỚI: Thêm Tab biểu đồ tròn ---
        tabbedPane.addTab("Rain Level (Pie Chart)", createRainPieChartPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createDashboardPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());

        JPanel pnlDisplay = new JPanel(new GridLayout(2, 1));
        pnlDisplay.setBackground(new Color(240, 248, 255));
        
        lblTemp = new JLabel("Temperature: -- °C | Humidity: -- % | Rain: --", SwingConstants.CENTER);
        lblTemp.setFont(new Font("Arial", Font.BOLD, 24));
        lblTemp.setForeground(new Color(0, 102, 204));
        
        lblStatus = new JLabel("Status: Connecting to MQTT...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 14));
        
        pnlDisplay.add(lblTemp);
        pnlDisplay.add(lblStatus);
        pnlMain.add(pnlDisplay, BorderLayout.NORTH);

        JPanel pnlControls = new JPanel(new GridLayout(1, 3, 20, 20));
        pnlControls.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        pnlControls.add(createDevicePanel("LED", "1", "0", new Color(255, 255, 204)));
        pnlControls.add(createDevicePanel("FAN", "2", "3", new Color(204, 255, 204)));
        pnlControls.add(createDevicePanel("BUZZER", "4", "5", new Color(204, 229, 255)));

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

        JButton btnOn = new JButton( deviceName +" ON");
        btnOn.setBackground(Color.WHITE);
        btnOn.setForeground(new Color(0, 150, 0));
        btnOn.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnOn.addActionListener(e -> {
            mqttManager.publish(onCmd);
            JOptionPane.showMessageDialog(this, "Command Send " + deviceName);
        });

        JButton btnOff = new JButton( deviceName + " OFF");
        btnOff.setBackground(Color.WHITE);
        btnOff.setForeground(Color.RED);
        btnOff.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnOff.addActionListener(e -> {
            mqttManager.publish(offCmd);
            JOptionPane.showMessageDialog(this, "Command Send " + deviceName);
        });

        panel.add(btnOn);
        panel.add(btnOff);
        return panel;
    }

    // --- Tab 2: Biểu đồ đường (Line Chart) ---
    private JPanel createChartPanel() {
        seriesTemp = new XYSeries("Temperature (°C)");
        XYSeriesCollection datasetTemp = new XYSeriesCollection(seriesTemp);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Environment Monitor", "Time", "Temperature",
                datasetTemp, PlotOrientation.VERTICAL, true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        
        NumberAxis rangeAxisTemp = (NumberAxis) plot.getRangeAxis();
        rangeAxisTemp.setRange(27.0, 31.0);
        rangeAxisTemp.setAutoRangeIncludesZero(false);

        XYLineAndShapeRenderer rendererTemp = new XYLineAndShapeRenderer();
        rendererTemp.setSeriesPaint(0, Color.RED);
        rendererTemp.setSeriesShapesVisible(0, true);
        plot.setRenderer(0, rendererTemp);

        // Setup Dataset 2 (Humidity)
        seriesHum = new XYSeries("Humidity (%)");
        XYSeriesCollection datasetHum = new XYSeriesCollection(seriesHum);
        
        NumberAxis rangeAxisHum = new NumberAxis("Humidity");
        rangeAxisHum.setRange(60.0, 70.0);
        rangeAxisHum.setAutoRangeIncludesZero(false);
        
        plot.setDataset(1, datasetHum); 
        plot.setRangeAxis(1, rangeAxisHum); 
        plot.mapDatasetToRangeAxis(1, 1);
        
        XYLineAndShapeRenderer rendererHum = new XYLineAndShapeRenderer();
        rendererHum.setSeriesPaint(0, Color.BLUE);
        rendererHum.setSeriesShapesVisible(0, true);
        plot.setRenderer(1, rendererHum);

        return new ChartPanel(chart);
    }

    // --- MỚI: Tab 3: Biểu đồ tròn (Pie Chart) cho Lượng mưa ---
    private JPanel createRainPieChartPanel() {
        rainDataset = new DefaultPieDataset();
        // Khởi tạo giá trị mặc định
        rainDataset.setValue("Rain Level", 0);
        rainDataset.setValue("Dry (Empty)", 100);

        JFreeChart chart = ChartFactory.createPieChart(
            "Current Rain Sensor Status",   // Tiêu đề
            rainDataset,                    // Dữ liệu
            true,                           // Hiển thị chú thích (Legend)
            true,
            false
        );

        // Tùy chỉnh màu sắc cho đẹp
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Rain Level", new Color(51, 153, 255)); // Màu xanh nước biển
        plot.setSectionPaint("Dry (Empty)", new Color(220, 220, 220)); // Màu xám nhạt
        
        // Hiển thị phần trăm
        plot.setSimpleLabels(true);

        return new ChartPanel(chart);
    }

    public void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            lblStatus.setText("Status: Online");
            lblStatus.setForeground(new Color(0, 150, 0));
        } else {
            lblStatus.setText("Status: Lost connection to MQTT!");
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

                    String rainStr = "0";
                    float rainVal = 0;
                    if (parts.length >= 3) {
                        rainStr = parts[2].trim();
                        rainVal = Float.parseFloat(rainStr);
                    }
                    
                    lblTemp.setText("Temperature: " + temp + " °C | Humidity: " + hum + "% | Rain: " + rainStr);

                    dbManager.saveSensorData(temp, hum, rainVal);

                    timeSecond += 10;
                    seriesTemp.add(timeSecond, temp);
                    seriesHum.add(timeSecond, hum);
                    
                    // --- MỚI: Cập nhật biểu đồ tròn ---
                    // Giả sử cảm biến trả về 0-100 (%), nếu dùng 0-1024 thì bạn chia tỉ lệ lại nhé
                    float maxVal = 100.0f; 
                    float rainDisplay = rainVal;
                    if (rainDisplay > maxVal) rainDisplay = maxVal; // Cắt trần nếu quá 100
                    
                    rainDataset.setValue("Rain Level", rainDisplay);
                    rainDataset.setValue("Dry (Empty)", maxVal - rainDisplay);
                    // ----------------------------------

                    // Gọi sang TelegramNotifier để kiểm tra logic 31.5 độ
                    boolean isDangerous = TelegramNotifier.checkAndAlertFire(temp);

                    if (isDangerous) {
                        lblStatus.setText(" Warning: Your house is burning! ");
                        lblStatus.setForeground(Color.RED);
                    } else {
                        lblStatus.setText("Status: Online");
                        lblStatus.setForeground(new Color(0, 150, 0));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        });
    }
}