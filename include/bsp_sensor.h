/**
 * @file       bsp_sensor.h
 * @copyright  None
 * @version    1.0.0
 * @date       2025-12-20
 * @author     Bach Pham
 * @brief      Sensor Modules Board Support Package (BSP) header file
 */

/* Define to prevent recursive inclusion ------------------------------ */
#ifndef BSP_SENSOR_H
#define BSP_SENSOR_H

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
 * @brief   Initialize sensor module
 * @note    Must be called before using other sensor functions
 * @return  true:  Initialization successful
 *          false: Initialization failed
 */
bool bsp_sensor_init(void);

/**
 * @brief   Get sensor data
 * @param[out] temp           Pointer to store temperature value
 * @param[out] hum            Pointer to store humidity value
 * @param[out] rain_percent   Pointer to store rain percentage value
 * @return     true:  Data retrieval successful
 *             false: Data retrieval failed
 */
bool bsp_sensor_get_data(float *temp, float *hum, int *rain_percent);

#ifdef __cplusplus
}
#endif
#endif

/* End of file -------------------------------------------------------- */