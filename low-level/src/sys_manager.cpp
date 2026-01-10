
/**
 * @file       main.cpp
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      System Manager implementation
 */

/* Includes ----------------------------------------------------------- */
#include "sys_manager.h"
#include "bsp_buzzer.h"
#include "bsp_fan.h"
#include "bsp_led.h"
#include "bsp_sensor.h"
#include "project_config.h"

#include <Arduino.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

/* Private defines ---------------------------------------------------- */
/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
WiFiClient espClient;
PubSubClient client(espClient);

int current_rain = 0;
float current_temp = 0;
float current_hum = 0;
bool manual_fan_override = false;

/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/**
 * @brief  Setup WiFi connection
 */
static void setup_wifi();

/**
 * @brief  Reconnect to MQTT broker
 */
static void reconnect();

/**
 * @brief  MQTT message callback
 * @param[in]  topic     Topic of the received message
 * @param[in]  payload   Payload of the received message
 * @param[in]  length    Length of the payload
 * @note    This function is called when a message is received on a subscribed topic
            Handle incoming commands
            +---------+------------------------+
            | Command | Action                 |
            +---------+------------------------+
            |   0     | Turn off LED           |
            |   1     | Turn on LED            |
            |   2     | Turn on Fan            |
            |   3     | Turn off Fan           |
            |   4     | Turn on Buzzer         |
            |   5     | Turn off Buzzer        |
            +---------+------------------------+

 */
static void mqtt_callback(char *topic, byte *payload, unsigned int length);

/**
 * @brief  Sensor data handling task
 * @param[in]  pvParam   Task parameter
 */
static void task_sensor(void *pvParam);

/**
 * @brief  Network handling task
 * @param[in]  pvParam   Task parameter
 */
static void task_network(void *pvParam);

/* Function definitions ----------------------------------------------- */
void system_init(void) {
  Serial.begin(115200);
  Serial.println("System Initializing ...");

  // Initialize BSP
  bsp_led_init();
  bsp_fan_init();
  bsp_buzzer_init();

  // Set default states
  bsp_led_set_rgb(0, 0, 0);
  bsp_fan_set_speed(0);
  bsp_buzzer_set(false);

  if (!bsp_sensor_init()) {
    Serial.println("Sensor Init Failed!");
  }

  // Create tasks
  xTaskCreatePinnedToCore(task_sensor, "Sensor_Task", 4096, NULL, 1, NULL, 1);
  xTaskCreatePinnedToCore(task_network, "Net_Task", 4096, NULL, 1, NULL, 0);
}

/* Private definitions ------------------------------------------------ */
static void mqtt_callback(char *topic, byte *payload, unsigned int length) {
  char msg[length + 1];
  memcpy(msg, payload, length);
  msg[length] = '\0';

  Serial.printf("[MQTT] Message arrived [%s]: %s\n", topic, msg);

  if (strcmp(topic, TOPIC_FAN_SET) == 0 || strcmp(topic, TOPIC_LED_SET) == 0) {
    int command = atoi(msg);
    switch (command) {
    case 1:
      bsp_led_set_rgb(255, 255, 255);
      Serial.println(" -> Command 1: Light ON");
      break;

    case 0:
      bsp_led_set_rgb(0, 0, 0);
      Serial.println(" -> Command 0: Light OFF");
      break;

    case 2:
      bsp_fan_set_speed(200);
      manual_fan_override = true;
      Serial.println(" -> Command 2: Fan ON");
      break;

    case 3:
      bsp_fan_set_speed(0);
      manual_fan_override = true;
      Serial.println(" -> Command 3: Fan OFF");
      break;

    case 4:
      bsp_buzzer_set(true);
      Serial.println(" -> Command 4: Buzzer ON");
      break;

    case 5:
      bsp_buzzer_set(false);
      Serial.println(" -> Command 5: Buzzer OFF");
      break;

    default:
      if (command > 10 && command <= 255) {
        if (command > 200)
          command = 200;
        bsp_fan_set_speed(command);
        manual_fan_override = true;
        Serial.printf(" -> Custom Fan Speed: %d\n", command);
      }
      break;
    }
  }
}

static void task_sensor(void *pvParam) {
  char json_buffer[100];
  while (1) {
    if (bsp_sensor_get_data(&current_temp, &current_hum, &current_rain)) {

      Serial.printf("Temp: %.2f C | Hum: %.2f %% | Rain: %d %%\n", current_temp,
                    current_hum, current_rain);

      if (client.connected()) {
        snprintf(json_buffer, sizeof(json_buffer), "%.2f;%.2f;%d", current_temp,
                 current_hum, current_rain);

        client.publish(TOPIC_SENSOR, json_buffer);
      }

      if (current_rain > 50) {
        Serial.println("WARNING: IT'S RAINING!");
        bsp_buzzer_set(true);
      }

      if (!manual_fan_override && current_temp > 32.0) {
        bsp_fan_set_speed(180);
        Serial.println("Auto Fan ON (High Temp)");
      }
    } else {
      Serial.println("Sensor Error!");
    }

    vTaskDelay(pdMS_TO_TICKS(5000));
  }
}

static void task_network(void *pvParam) {
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

static void setup_wifi() {
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

static void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    if (client.connect(clientId.c_str(), MQTT_USER, MQTT_PASS)) {
      Serial.println("connected");
      client.subscribe(TOPIC_FAN_SET);
      client.subscribe(TOPIC_LED_SET);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      vTaskDelay(pdMS_TO_TICKS(5000));
    }
  }
}

/* End of file -------------------------------------------------------- */