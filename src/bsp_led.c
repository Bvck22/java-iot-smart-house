#include "bsp_led.h"
#include "project_config.h"
#include <Arduino.h>

void bsp_led_init(void) {
  pinMode(PIN_LED_R, OUTPUT);
  pinMode(PIN_LED_G, OUTPUT);
  pinMode(PIN_LED_B, OUTPUT);
}

void bsp_led_set_rgb(uint8_t r, uint8_t g, uint8_t b) {
  analogWrite(PIN_LED_R, 255 - r);
  analogWrite(PIN_LED_G, 255 - g);
  analogWrite(PIN_LED_B, 255 - b);
}