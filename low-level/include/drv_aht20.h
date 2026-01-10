/**
 * @file       drv_aht20.h
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      AHT20 Temperature and Humidity Sensor Driver Header File
 */

/* Define to prevent recursive inclusion ------------------------------ */
#ifndef DRV_AHT20_H
#define DRV_AHT20_H

/* Includes ----------------------------------------------------------- */
#include "driver/i2c.h"
#include <stdbool.h>
#include <stdint.h>


/* Public defines ----------------------------------------------------- */
#ifdef __cplusplus
extern "C" {
#endif

/* Public enumerate/structure ----------------------------------------- */
/* Public macros ------------------------------------------------------ */
/* Public variables --------------------------------------------------- */
/* Public function prototypes ----------------------------------------- */
/**
 * @brief   Initialize AHT20 sensor
 *
 * @param[in]  sda_pin   GPIO pin number for I2C SDA
 * @param[in]  scl_pin   GPIO pin number for I2C SCL
 *
 * @return     ESP_OK:    Initialization successful
 *             ESP_FAIL:  Initialization failed
 */
esp_err_t drv_aht20_init(int sda_pin, int scl_pin);

/**
 * @brief   Read temperature and humidity from AHT20 sensor
 *
 * @param[out]  temperature   Pointer to store temperature value in Celsius
 * @param[out]  humidity      Pointer to store humidity value in percentage
 *
 * @return     ESP_OK:    Read successful
 *             ESP_FAIL:  Read failed
 */
esp_err_t drv_aht20_read(float *temperature, float *humidity);

#ifdef __cplusplus
}
#endif
#endif

/* End of file -------------------------------------------------------- */