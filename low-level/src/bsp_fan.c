/**
 * @file       bsp_fan.c
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      Fan Motor Module Board Support Package implementation
 */

/* Includes ----------------------------------------------------------- */
#include "bsp_fan.h"
#include "project_config.h"
#include <Arduino.h>

/* Private defines ---------------------------------------------------- */
/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/* Function definitions ----------------------------------------------- */
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

/* Private definitions ----------------------------------------------- */
/* End of file -------------------------------------------------------- */