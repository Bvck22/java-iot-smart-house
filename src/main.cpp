/**
 * @file       main.cpp
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      Main application
 */

/* Includes ----------------------------------------------------------- */
#include "soc/rtc_cntl_reg.h"
#include "soc/soc.h"
#include "sys_manager.h"
#include <Arduino.h>

/* Private defines ---------------------------------------------------- */
/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/* Function definitions ----------------------------------------------- */
void setup() {
  system_init();
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0);
}

void loop() { vTaskDelete(NULL); }

/* Private definitions ----------------------------------------------- */
/* End of file -------------------------------------------------------- */