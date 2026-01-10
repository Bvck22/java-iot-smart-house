/**
 * @file       bsp_buzzer.h
 * @copyright  None
 * @version    1.0.0
 * @date       2025-20-12
 * @author     Bach Pham
 * @brief      Buzzer Board Support Package (BSP) header file
 */

/* Define to prevent recursive inclusion ------------------------------ */
#ifndef BSP_BUZZER_H
#define BSP_BUZZER_H

/* Includes ----------------------------------------------------------- */
#include <stdbool.h>

/* Public defines ----------------------------------------------------- */
#ifdef __cplusplus
extern "C" {
#endif

/* Public enumerate/structure ----------------------------------------- */
/* Public macros ------------------------------------------------------ */
/* Public variables --------------------------------------------------- */
/* Public function prototypes ----------------------------------------- */
/**
 * @brief  Initialize the buzzer hardware
 * @note   This function must be called before using the buzzer
 */
void bsp_buzzer_init(void);

/**
 * @brief  Set the buzzer state
 * @param  on: true to turn on the buzzer, false to turn it off
 */
void bsp_buzzer_set(bool on);

#ifdef __cplusplus
}
#endif
#endif

/* End of file -------------------------------------------------------- */