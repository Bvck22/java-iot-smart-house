

public class SmartApp {
    public static void main(String[] args) {
        System.out.println(">>> KHOI DONG HE THONG AI DO LAP <<<");

        // Không cần MqttManager nữa
        // Chạy thẳng giao diện SmartDashboardUI độc lập
        SmartDashboardUI smartGui = new SmartDashboardUI();
        smartGui.setVisible(true);
    }
}