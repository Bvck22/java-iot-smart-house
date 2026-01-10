/**
 * @file       bsp_led.h
 * @copyright  None
 * @version    1.0.0
 * @date       2025-20-12
 * @author     Bach Pham
 * @brief      RGB LED Board Support Package (BSP) Header File
 */

/* Define to prevent recursive inclusion ------------------------------ */
#ifndef BSP_LED_H
#define BSP_LED_H

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
 * @brief  Initialize LED BSP
 * @note   This function must be called before using any other LED BSP function
 */
void bsp_led_init(void);

/**
 * @brief  Set RGB LED color using hexadecimal value
 * @param  r: Red component (0-255)
 * @param  g: Green component (0-255)
 * @param  b: Blue component (0-255)
 */
void bsp_led_set_rgb(uint8_t r, uint8_t g, uint8_t b);

#ifdef __cplusplus
}
#endif
#endif

/* End of file -------------------------------------------------------- */