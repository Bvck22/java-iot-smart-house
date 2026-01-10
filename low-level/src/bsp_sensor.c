/**
 * @file       bsp_led.c
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      Sensor Modules Board Support Package implementation
 */

/* Includes ----------------------------------------------------------- */
#include "bsp_sensor.h"
#include "drv_aht20.h"
#include "project_config.h"
#include <Arduino.h>

/* Private defines ---------------------------------------------------- */
/* Private enumerate/structure ---------------------------------------- */
/* Private macros ----------------------------------------------------- */
/* Public variables --------------------------------------------------- */
/* Private variables -------------------------------------------------- */
/* Private function prototypes ---------------------------------------- */
/**
 * @brief   Custom map function to scale values
 * @param[in] x Input value to be mapped
 * @param[in] in_min Minimum of input range
 * @param[in] in_max Maximum of input range
 * @param[in] out_min Minimum of output range
 * @param[in] out_max Maximum of output range
 * @return    Mapped output value
 */
static long map_custom(long x, long in_min, long in_max, long out_min,
                       long out_max);

/* Function definitions ----------------------------------------------- */
bool bsp_sensor_init(void) {
  // Configure rain sensor pins
  pinMode(PIN_RAIN_SIGNAL, INPUT_PULLUP);
  pinMode(PIN_RAIN_POWER, OUTPUT);

  // Power off at start
  digitalWrite(PIN_RAIN_POWER, LOW);

  // Check initialization
  if (drv_aht20_init(I2C_MASTER_SDA_IO, I2C_MASTER_SCL_IO) == ESP_OK) {
    return true;
  }
  return false;
}

bool bsp_sensor_get_data(float *temp, float *hum, int *rain_percent) {
  // Check pointers
  if (drv_aht20_read(temp, hum) != ESP_OK) {
    return false;
  }

  // Turn on rain sensor
  digitalWrite(PIN_RAIN_POWER, HIGH);
  delay(20);

  // Read RAW value
  int raw_rain = analogRead(PIN_RAIN_SIGNAL);

  // Turn off rain sensor
  digitalWrite(PIN_RAIN_POWER, LOW);

  // Map to percentage
  int percent = map_custom(raw_rain, 0, 2048, 0, 100);

  if (percent < 0)
    percent = 0;
  if (percent > 100)
    percent = 100;

  *rain_percent = percent;
  return true;
}

/* Private definitions ----------------------------------------------- */
static long map_custom(long x, long in_min, long in_max, long out_min,
                       long out_max) {
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
/* End of file -------------------------------------------------------- */