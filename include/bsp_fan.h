#ifndef BSP_FAN_H
#define BSP_FAN_H
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

void bsp_fan_init(void);
void bsp_fan_set_speed(uint8_t speed);

#ifdef __cplusplus
}
#endif
#endif