#include "bsp_sensor.h"
#include "drv_aht20.h"
#include "project_config.h"
#include <Arduino.h>

long map_custom(long x, long in_min, long in_max, long out_min, long out_max) {
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

bool bsp_sensor_init(void) {
    pinMode(PIN_RAIN_SIGNAL, INPUT_PULLUP); 
    
    pinMode(PIN_RAIN_POWER, OUTPUT);
    digitalWrite(PIN_RAIN_POWER, LOW); 

    if (drv_aht20_init(I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO) == ESP_OK) {
        return true;
    }
    return false;
}

bool bsp_sensor_get_data(float *temp, float *hum, int *rain_percent) {

    if (drv_aht20_read(temp, hum) != ESP_OK) {
        return false;
    }

    digitalWrite(PIN_RAIN_POWER, HIGH);
    delay(20);

    int raw_rain = analogRead(PIN_RAIN_SIGNAL);

    digitalWrite(PIN_RAIN_POWER, LOW);

    int percent = map_custom(raw_rain, 0, 2048, 0, 100);

    if (percent < 0) percent = 0;
    if (percent > 100) percent = 100;

    *rain_percent = percent;
    return true;
}