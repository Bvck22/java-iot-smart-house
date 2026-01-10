import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class AiDataLoader {
    // Tên file dữ liệu (Ông nhớ để file này chung folder với code)
    private static final String CSV_FILE = "data.csv";
    
    // Biến này để nhớ vị trí đang đọc (giúp giả lập hiệu ứng thời gian trôi)
    private static int currentIndex = 0; 
    private List<SensorData> fullDataset;

    public AiDataLoader() {
        // Khi khởi động, load toàn bộ CSV vào RAM luôn cho nhanh
        fullDataset = new ArrayList<>();
        loadCsvData();
    }

    private void loadCsvData() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); // Đọc bỏ dòng tiêu đề (Header) đầu tiên
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Cấu trúc file CSV của ông: 
                // index 0=id, 1=temperature, 2=humidity, 3=created_at
                if (parts.length >= 3) {
                    try {
                        float temp = Float.parseFloat(parts[1].trim());
                        float hum = Float.parseFloat(parts[2].trim());
                        fullDataset.add(new SensorData(temp, hum));
                    } catch (NumberFormatException e) {
                        // Bỏ qua dòng lỗi nếu có
                    }
                }
            }
            System.out.println(">> Da nap thanh cong " + fullDataset.size() + " dong du lieu tu CSV!");
        } catch (Exception e) {
            System.err.println("LOI: Khong doc duoc file data.csv. HHay chac chan file nam dung cho!");
            e.printStackTrace();
        }
    }

    public List<SensorData> fetchRecentData(int limit) {
        // Hàm này giả lập việc lấy dữ liệu mới nhất
        List<SensorData> result = new ArrayList<>();
        
        if (fullDataset.isEmpty()) return result;

        // Nếu chạy hết dữ liệu trong file thì quay vòng lại từ đầu (Loop)
        if (currentIndex + limit >= fullDataset.size()) {
            currentIndex = 0;
        }

        // Cắt ra 1 đoạn dữ liệu (window) từ vị trí hiện tại
        for (int i = 0; i < limit; i++) {
            result.add(fullDataset.get(currentIndex + i));
        }

        // Tăng chỉ số để lần sau lấy dòng tiếp theo -> Tạo hiệu ứng đồ thị di chuyển
        currentIndex++; 

        return result;
    }
}