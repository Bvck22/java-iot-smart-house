#include "bsp_buzzer.h"
#include "project_config.h"
#include <Arduino.h>

#define BUZZER_CHANNEL 0 

void bsp_buzzer_init(void) {
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