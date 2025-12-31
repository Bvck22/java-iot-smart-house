#ifndef BSP_LED_H
#define BSP_LED_H
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

void bsp_led_init(void);
void bsp_led_set_rgb(uint8_t r, uint8_t g, uint8_t b);

#ifdef __cplusplus
}
#endif
#endif