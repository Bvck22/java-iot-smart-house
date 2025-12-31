#ifndef BSP_BUZZER_H
#define BSP_BUZZER_H
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

void bsp_buzzer_init(void);
void bsp_buzzer_set(bool on);

#ifdef __cplusplus
}
#endif
#endif