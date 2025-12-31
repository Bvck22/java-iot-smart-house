#include "bsp_fan.h"
#include "project_config.h"
#include <Arduino.h>

void bsp_fan_init(void) {
  pinMode(PIN_FAN_INA, OUTPUT);
  pinMode(PIN_FAN_INB, OUTPUT);
  digitalWrite(PIN_FAN_INA, LOW);
  digitalWrite(PIN_FAN_INB, LOW);
}

void bsp_fan_set_speed(uint8_t speed) {
  analogWrite(PIN_FAN_INA, speed);
  digitalWrite(PIN_FAN_INB, LOW);
}