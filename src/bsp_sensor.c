#include "bsp_sensor.h"
#include "drv_aht20.h"
#include "project_config.h"

bool bsp_sensor_init(void) {
  if (drv_aht20_init(I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO) == ESP_OK) {
    return true;
  }
  return false;
}

bool bsp_sensor_get_data(float *temp, float *hum) {
  if (drv_aht20_read(temp, hum) == ESP_OK) {
    return true;
  }
  return false;
}