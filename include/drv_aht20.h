#ifndef DRV_AHT20_H
#define DRV_AHT20_H

#include <stdint.h>
#include <stdbool.h>
#include "driver/i2c.h"

#ifdef __cplusplus
extern "C" {
#endif

esp_err_t drv_aht20_init(int sda_pin, int scl_pin);

esp_err_t drv_aht20_read(float *temperature, float *humidity);

#ifdef __cplusplus
}
#endif

#endif