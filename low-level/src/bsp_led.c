/**
 * @file       bsp_led.c
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      RGB LED Board Support Package implementation
 */

/* Includes ----------------------------------------------------------- */
#include "bsp_led.h"
#include "project_config.h"
#include <Arduino.h>

/* Private defines ---------------------------------------------------- */
/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/* Function definitions ----------------------------------------------- */
void bsp_led_init(void) {
  pinMode(PIN_LED_R, OUTPUT);
  pinMode(PIN_LED_G, OUTPUT);
  pinMode(PIN_LED_B, OUTPUT);
}

void bsp_led_set_rgb(uint8_t r, uint8_t g, uint8_t b) {
  analogWrite(PIN_LED_R, r);
  analogWrite(PIN_LED_G, g);
  analogWrite(PIN_LED_B, b);
}

/* Private definitions ----------------------------------------------- */
/* End of file -------------------------------------------------------- */