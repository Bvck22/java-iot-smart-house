#ifndef PROJECT_CONFIG_H
#define PROJECT_CONFIG_H

// I2C PINS
#define I2C_MASTER_SDA_IO 21
#define I2C_MASTER_SCL_IO 22
#define I2C_MASTER_FREQ_HZ 100000

// FAN PINS
#define PIN_FAN_INA 16
#define PIN_FAN_INB 17

// LED PINS
#define PIN_LED_R 25
#define PIN_LED_G 26
#define PIN_LED_B 27

// WIFI CONFIG
#define WIFI_SSID       "C14.04"            // WIFI NAME
#define WIFI_PASSWORD   "chungcubaton"      // WIFI PASSWORD

// MQTT CONFIG
#define MQTT_SERVER     "broker.hivemq.com" // IP MQTT Broker
#define MQTT_PORT       1883                // PORT MQTT Broker
#define MQTT_USER       ""                  // Blank if none
#define MQTT_PASS       ""                  // Blank if none

// --- MQTT TOPICS ---
#define TOPIC_SENSOR    "smarthome/k22/sensor" 
#define TOPIC_FAN_SET   "smarthome/k22/control"
/* Reserved for future use
#define TOPIC_LED_SET   "smarthome/k22/led/set"
*/

#endif