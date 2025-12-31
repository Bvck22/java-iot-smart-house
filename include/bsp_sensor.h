#ifndef BSP_SENSOR_H
#define BSP_SENSOR_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

bool bsp_sensor_init(void);
bool bsp_sensor_get_data(float *temp, float *hum, int *rain_percent);

#ifdef __cplusplus
}
#endif
#endif