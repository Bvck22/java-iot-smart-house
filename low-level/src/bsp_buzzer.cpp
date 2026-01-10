/**
 * @file       bsp_buzzer.cpp
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      Buzzer Board Support Package implementation
 */

/* Includes ----------------------------------------------------------- */
#include "bsp_buzzer.h"
#include "project_config.h"
#include <Arduino.h>

/* Private defines ---------------------------------------------------- */
#define BUZZER_CHANNEL 0

/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/* Function definitions ----------------------------------------------- */
void bsp_buzzer_init(void) {
  /*
      Configure LEDC for buzzer control
      2000 Hz frequency, 8-bit resolution
  */
  ledcSetup(BUZZER_CHANNEL, 2000, 8);
  ledcAttachPin(PIN_BUZZER, BUZZER_CHANNEL);
  ledcWriteTone(BUZZER_CHANNEL, 0);
}

void bsp_buzzer_set(bool on) {
  if (on) {
    ledcWriteTone(BUZZER_CHANNEL, 2000);
  } else {
    ledcWriteTone(BUZZER_CHANNEL, 0);
  }
}

/* Private definitions ----------------------------------------------- */
/* End of file -------------------------------------------------------- */