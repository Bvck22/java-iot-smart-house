#include "sys_manager.h"
#include "project_config.h"
#include "bsp_sensor.h"
#include "bsp_fan.h"
#include "bsp_led.h"
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <Arduino.h>
#include <WiFi.h>
#include <PubSubClient.h>

WiFiClient espClient;
PubSubClient client(espClient);

float current_temp = 0;
float current_hum = 0;
bool manual_fan_override = false;

void setup_wifi();
void reconnect();
void mqtt_callback(char* topic, byte* payload, unsigned int length);

void mqtt_callback(char* topic, byte* payload, unsigned int length) {
    char msg[length + 1];
    memcpy(msg, payload, length);
    msg[length] = '\0';

    Serial.printf("[MQTT] Message arrived [%s]: %s\n", topic, msg);

    if (strcmp(topic, TOPIC_FAN_SET) == 0) {
        int speed = atoi(msg);
        if (speed < 0) speed = 0;
        if (speed > 255) speed = 255;
        
        bsp_fan_set_speed(speed);
        manual_fan_override = true;
        Serial.printf(" -> Set Fan Speed: %d\n", speed);
    }
/* Reserved for future use
    if (strcmp(topic, TOPIC_LED_SET) == 0) {
        int r, g, b;
        if (sscanf(msg, "%d,%d,%d", &r, &g, &b) == 3) {
            bsp_led_set_rgb(r, g, b);
            Serial.printf(" -> Set LED RGB: %d %d %d\n", r, g, b);
        }
    }
*/
}

void task_sensor(void *pvParam) {
    char json_buffer[100];
    
    while (1) {
        if (bsp_sensor_get_data(&current_temp, &current_hum)) {
            Serial.printf("Temp: %.2f C | Hum: %.2f %%\n", current_temp, current_hum);

            if (client.connected()) {
                snprintf(json_buffer, sizeof(json_buffer), 
                         "{\"temp\": %.2f, \"hum\": %.2f}", current_temp, current_hum);
                client.publish(TOPIC_SENSOR, json_buffer);
            }

            if (!manual_fan_override && current_temp > 32.0) {
                bsp_fan_set_speed(180); // Tự bật cứu hộ
                Serial.println(" -> Auto Fan ON (High Temp)");
            }
        } 
        else {
            Serial.println("Sensor Error!");
        }
        vTaskDelay(pdMS_TO_TICKS(2000));
    }
}

void task_network(void *pvParam) {
    setup_wifi();
    client.setServer(MQTT_SERVER, MQTT_PORT);
    client.setCallback(mqtt_callback);

    while (1) {
        if (!client.connected()) {
            reconnect();
        }
        client.loop();
        vTaskDelay(pdMS_TO_TICKS(10));
    }
}

void setup_wifi() {
    delay(10);
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(WIFI_SSID);

    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }
    Serial.println("");
    Serial.println("WiFi connected");
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
}

void reconnect() {
    while (!client.connected()) {
        Serial.print("Attempting MQTT connection...");
        String clientId = "ESP32Client-";
        clientId += String(random(0xffff), HEX);
        if (client.connect(clientId.c_str(), MQTT_USER, MQTT_PASS)) {
            Serial.println("connected");
            client.subscribe(TOPIC_FAN_SET);
            /* Reserved for future use
            client.subscribe(TOPIC_LED_SET);
            */
        } else {
            Serial.print("failed, rc=");
            Serial.print(client.state());
            Serial.println(" try again in 5 seconds");
            vTaskDelay(pdMS_TO_TICKS(5000));
        }
    }
}

void system_init(void) {
    Serial.begin(115200);
    Serial.println("System Initializing...");

    bsp_led_init();
    bsp_fan_init();

    bsp_led_set_rgb(0, 0, 0); 
    bsp_fan_set_speed(0);

    if (!bsp_sensor_init()) {
        Serial.println("Sensor Init Failed!");
    }

    xTaskCreatePinnedToCore(task_sensor, "Sensor_Task", 4096, NULL, 1, NULL, 1);
    xTaskCreatePinnedToCore(task_network, "Net_Task", 4096, NULL, 1, NULL, 0);
}