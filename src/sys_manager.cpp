#include "bsp_fan.h"
#include "bsp_led.h"
#include "bsp_sensor.h"
#include "sys_manager.h"
#include <Arduino.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>


void hue_to_rgb(uint8_t hue, uint8_t *r, uint8_t *g, uint8_t *b) {
  uint8_t region, remainder, p, q, t;
  if (hue == 255)
    hue = 0;
  region = hue / 43;
  remainder = (hue - (region * 43)) * 6;
  p = 0;
  q = (255 * (255 - remainder)) >> 8;
  t = (255 * remainder) >> 8;
  switch (region) {
  case 0:
    *r = 255;
    *g = t;
    *b = p;
    break;
  case 1:
    *r = 255 - t;
    *g = 255;
    *b = p;
    break;
  case 2:
    *r = p;
    *g = 255;
    *b = t;
    break;
  case 3:
    *r = p;
    *g = 255 - t;
    *b = 255;
    break;
  case 4:
    *r = t;
    *g = p;
    *b = 255;
    break;
  default:
    *r = 255;
    *g = p;
    *b = 255 - t;
    break;
  }
}

void task_led(void *pvParam) {
  uint8_t hue = 0;
  uint8_t r, g, b;
  while (1) {
    hue_to_rgb(hue++, &r, &g, &b);
    bsp_led_set_rgb(r, g, b);
    vTaskDelay(pdMS_TO_TICKS(50));
  }
}

void task_sensor(void *pvParam) {
  float t, h;
  while (1) {
    if (bsp_sensor_get_data(&t, &h)) {
      Serial.printf("Temp: %.2f C | Hum: %.2f %%\n", t, h);

      if (t > 30.0) {
        bsp_fan_set_speed(255);
        Serial.println(" -> Fan MAX");
      } else if (t > 28.0) {
        bsp_fan_set_speed(150);
        Serial.println(" -> Fan MED");
      } else {
        bsp_fan_set_speed(0);
        Serial.println(" -> Fan OFF");
      }
    } else {
      Serial.println("Sensor Error!");
    }
    vTaskDelay(pdMS_TO_TICKS(2000));
  }
}

void system_init(void) {
  Serial.begin(115200);
  Serial.println("System Initializing...");

  bsp_led_init();
  bsp_fan_init();
  if (!bsp_sensor_init()) {
    Serial.println("Sensor Init Failed!");
  }

  xTaskCreate(task_led, "LED_Task", 2048, NULL, 1, NULL);
  xTaskCreate(task_sensor, "Sensor_Task", 4096, NULL, 2, NULL);
}