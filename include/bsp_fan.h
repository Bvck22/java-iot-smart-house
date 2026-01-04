/**
 * @file       bsp_fan.h
 * @copyright  None
 * @version    1.0.0
 * @date       2025-20-12
 * @author     Bach Pham
 * @brief      Fan motor module Board Support Package (BSP) header file
 */

/* Define to prevent recursive inclusion ------------------------------ */
#ifndef BSP_FAN_H
#define BSP_FAN_H

/* Includes ----------------------------------------------------------- */
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
 * @brief  Initialize the fan hardware
 * @note   This function must be called before using the fan
 */
void bsp_fan_init(void);

/**
 * @brief  Set the fan speed
 * @param  speed: Fan speed value (0-255)
 */
void bsp_fan_set_speed(uint8_t speed);

#ifdef __cplusplus
}
#endif
#endif

/* End of file -------------------------------------------------------- */