#include "soc/rtc_cntl_reg.h"
#include "soc/soc.h"
#include "sys_manager.h"
#include <Arduino.h>


void setup() {
  system_init();
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0);
}

void loop() { vTaskDelete(NULL); }