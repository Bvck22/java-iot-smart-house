import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SmartDashboardUI extends JFrame {

    private JLabel lblPrediction; // Nhãn dự báo
    private JLabel lblSimulateLog;
    private JLabel lblAiStatus;
    
    private AiDataLoader dataLoader;
    private AiProcessor aiProcessor;
    private ScheduledExecutorService scheduler;

    public SmartDashboardUI() {
        setTitle("AI Monitor - DU BAO TUONG LAI");
        setSize(600, 500); // Cửa sổ to hơn
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Chia giao diện làm 3 phần đều nhau
        setLayout(new GridLayout(3, 1)); 

        this.dataLoader = new AiDataLoader();
        this.aiProcessor = new AiProcessor();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        initAiInterface(); 
        startAiLoop();     
    }

    private void initAiInterface() {
        // 1. LOG (Trên cùng)
        lblSimulateLog = new JLabel("Dang cho du lieu...");
        lblSimulateLog.setHorizontalAlignment(SwingConstants.CENTER);
        lblSimulateLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblSimulateLog.setBorder(BorderFactory.createTitledBorder("Nhat ky hoat dong"));

        // 2. DỰ BÁO (Ở giữa) - CHỮ TO
        lblPrediction = new JLabel("DANG TINH TOAN...");
        lblPrediction.setFont(new Font("Arial", Font.BOLD, 24)); // Chữ siêu to
        lblPrediction.setForeground(new Color(0, 102, 204)); // Màu xanh dương đậm
        lblPrediction.setHorizontalAlignment(SwingConstants.CENTER);
        lblPrediction.setBorder(BorderFactory.createTitledBorder("DU BAO 15 PHUT TOI"));

        // 3. TRẠNG THÁI (Dưới cùng)
        lblAiStatus = new JLabel("AI System: Khoi dong...");
        lblAiStatus.setFont(new Font("Arial", Font.BOLD, 16));
        lblAiStatus.setForeground(Color.WHITE);
        lblAiStatus.setOpaque(true);
        lblAiStatus.setBackground(Color.DARK_GRAY);
        lblAiStatus.setHorizontalAlignment(SwingConstants.CENTER);

        // Thêm 3 cái vào cửa sổ
        this.add(lblSimulateLog);
        this.add(lblPrediction);
        this.add(lblAiStatus);
    }

    private void startAiLoop() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<SensorData> data = dataLoader.fetchRecentData(20);

                if (!data.isEmpty()) {
                    SensorData latest = data.get(0); 
                    float temp = latest.getTemp();

                    // --- TÍNH TOÁN ---
                    float predictedTemp = aiProcessor.predictNextTemp(data);
                    String statusText = aiProcessor.analyze(data);

                    // --- CẬP NHẬT GIAO DIỆN ---
                    SwingUtilities.invokeLater(() -> {
                        // Cập nhật Log
                        lblSimulateLog.setText("Hien tai: " + temp + "C");

                        // Cập nhật DỰ BÁO (Quan trọng nhất)
                        String xuHuong = "";
                        if (predictedTemp > temp) {
                            xuHuong = "Dang tang";
                            lblPrediction.setForeground(Color.RED);
                        } else if (predictedTemp < temp) {
                            xuHuong = "Dang giam";
                            lblPrediction.setForeground(new Color(0, 153, 0)); // Xanh lá
                        } else {
                            xuHuong = "On dinh";
                            lblPrediction.setForeground(Color.BLUE);
                        }
                        
                        lblPrediction.setText(String.format("%.2f C (%s)", predictedTemp, xuHuong));

                        // Cập nhật Status
                        lblAiStatus.setText(statusText);
                        if (statusText.contains("AC")) {
                            lblAiStatus.setBackground(Color.RED);
                        } else {
                            lblAiStatus.setBackground(new Color(0, 153, 0));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 3, TimeUnit.SECONDS);
    }
}