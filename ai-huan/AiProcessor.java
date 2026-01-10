import java.util.List;

public class AiProcessor {

    public String analyze(List<SensorData> history) {
        if (history == null || history.isEmpty()) return "Cho du lieu...";
        
        SensorData current = history.get(0);
        float t = current.getTemp();
        float p = predictNextTemp(history);

        // Đổi chữ "AI Phan Tich" để ông biết là code mới
        String msg = "AI PHAN TICH: "; 

        if (t > 30.0) msg += "QUA NONG -> Bat AC gap! ";
        else if (p > 30.0) msg += "Du bao nong -> Bat AC som. ";
        else msg += "Mat me -> Bat quat. ";
        
        return msg;
    }

    // Hàm dự đoán xu hướng
    public float predictNextTemp(List<SensorData> history) {
        if (history.size() < 2) return history.get(0).getTemp(); // Nếu ít dữ liệu quá thì lấy luôn số hiện tại

        int n = Math.min(history.size(), 10); 
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;

        for (int i = 0; i < n; i++) {
            float y = history.get(n - 1 - i).getTemp(); 
            int x = i; 
            sumX += x; sumY += y; sumXY += x * y; sumXX += x * x;
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double b = (sumY - m * sumX) / n;
        return (float) (m * n + b);
    }
}