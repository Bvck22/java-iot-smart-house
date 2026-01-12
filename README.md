# IoT Smart House (Java + ESP32)

![ESP32](https://img.shields.io/badge/ESP32-E7352C?style=for-the-badge&logo=espressif&logoColor=white)
![MQTT](https://img.shields.io/badge/MQTT-3C2763?style=for-the-badge&logo=mqtt&logoColor=white)
![Java 17 | 21](https://img.shields.io/badge/Java_17_%7C_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![VS Code](https://img.shields.io/badge/VS_Code-0078D4?style=for-the-badge&logo=visualstudiocode&logoColor=white)
![PlatformIO](https://img.shields.io/badge/PlatformIO-F58225?style=for-the-badge&logo=platformio&logoColor=white)

Smart home system with three parts: a Java desktop app for control/monitoring over MQTT, an AI module forecasting temperature from CSV data, and ESP32 firmware to drive LED/Fan/Buzzer, read sensors, and publish sensor data.

## Quick Architecture
- Control app: Java Swing connects to a public MQTT broker, shows temperature/humidity/rain charts, logs to MySQL Cloud, sends Telegram alerts. Main code: [application/src/main/java/com/smarthome/App.java](application/src/main/java/com/smarthome/App.java).
- AI simulator: reads [ai-huan/data.csv](ai-huan/data.csv), predicts temperature, and suggests actions. Runs standalone on Java 17. Main code: [ai-huan/SmartApp.java](ai-huan/SmartApp.java).
- Device firmware: ESP32 DOIT DevKit V1 with Arduino framework, MQTT topics `smarthome/k22/control` (commands) and `smarthome/k22/sensor` (sensor data). Config: [low-level/platformio.ini](low-level/platformio.ini), main code: [low-level/src/main.cpp](low-level/src/main.cpp).

## Requirements
- JDK 21 for `application`; JDK 17 for `ai-huan`.
- Maven 3.9+.
- PlatformIO CLI (or VS Code PlatformIO) to flash ESP32.
- Default MQTT broker: `tcp://broker.hivemq.com:1883` (can be changed in [application/src/main/java/com/smarthome/MqttManager.java](application/src/main/java/com/smarthome/MqttManager.java)).
- MySQL Cloud and Telegram Bot Token are hard-coded in [application/src/main/java/com/smarthome/DatabaseManager.java](application/src/main/java/com/smarthome/DatabaseManager.java) and [application/src/main/java/com/smarthome/TelegramNotifier.java](application/src/main/java/com/smarthome/TelegramNotifier.java); replace with your own before running.

## Quick Start
### 1) Control App (MQTT Dashboard)
```bash
cd application
mvn clean package
mvn exec:java -Dexec.mainClass="com.smarthome.App"
```
GUI opens with 3 tabs: device control, temperature/humidity line charts, rain pie chart. Control commands use codes: LED on/off (1/0), FAN on/off (2/3), BUZZER on/off (4/5).

### 2) AI Simulator
```bash
cd ai-huan
mvn clean package
mvn exec:java -Dexec.mainClass="SmartApp"
```
UI shows CSV ingestion logs, 15-minute temperature prediction, and action suggestions.

### 3) ESP32 Firmware
- Plug in ESP32 DOIT DevKit V1, note the serial port.
- Update Wi-Fi, MQTT topic, and GPIO pins in code if needed (see [low-level/src](low-level/src)).
```bash
cd low-level
platformio run --target upload
```
<img width="655" height="740" alt="image" src="https://github.com/user-attachments/assets/83b1676a-b2c6-4109-a0d7-533c48fa40cd" />


## Folder Structure
- [application](application): Java 21 Swing + MQTT + MySQL + Telegram.
- [ai-huan](ai-huan): Java 17 AI CSV forecaster.
- [low-level](low-level): ESP32 Arduino/PlatformIO firmware.

## MQTT Topics
- Control publish: topic `smarthome/k22/control`, payload is device code.
- Sensor subscribe: topic `smarthome/k22/sensor`, payload format `temp;hum;rain` (e.g., `28.5;65.0;10`).

## Security Notes
- Replace Telegram Bot Token, Chat ID, Wi-Fi credentials, and MySQL credentials before release.
- If using a public broker, avoid sensitive data; consider a private broker with auth/TLS.

## License
None
