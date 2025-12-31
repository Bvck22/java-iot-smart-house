#include "drv_aht20.h"
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

#define AHT20_ADDR 0x38
#define I2C_PORT I2C_NUM_0

static esp_err_t send_cmd(uint8_t cmd, uint8_t d1, uint8_t d2) {
  i2c_cmd_handle_t link = i2c_cmd_link_create();
  i2c_master_start(link);
  i2c_master_write_byte(link, (AHT20_ADDR << 1) | I2C_MASTER_WRITE, true);
  i2c_master_write_byte(link, cmd, true);
  i2c_master_write_byte(link, d1, true);
  i2c_master_write_byte(link, d2, true);
  i2c_master_stop(link);
  esp_err_t ret = i2c_master_cmd_begin(I2C_PORT, link, pdMS_TO_TICKS(1000));
  i2c_cmd_link_delete(link);
  return ret;
}

esp_err_t drv_aht20_init(int sda_pin, int scl_pin) {
  i2c_config_t conf = {
      .mode = I2C_MODE_MASTER,
      .sda_io_num = sda_pin,
      .scl_io_num = scl_pin,
      .sda_pullup_en = GPIO_PULLUP_ENABLE,
      .scl_pullup_en = GPIO_PULLUP_ENABLE,
      .master.clk_speed = 100000,
  };
  i2c_param_config(I2C_PORT, &conf);
  i2c_driver_install(I2C_PORT, conf.mode, 0, 0, 0);

  vTaskDelay(pdMS_TO_TICKS(40));

  uint8_t status;
  i2c_cmd_handle_t link = i2c_cmd_link_create();
  i2c_master_start(link);
  i2c_master_write_byte(link, (AHT20_ADDR << 1) | I2C_MASTER_READ, true);
  i2c_master_read_byte(link, &status, I2C_MASTER_NACK);
  i2c_master_stop(link);
  i2c_master_cmd_begin(I2C_PORT, link, pdMS_TO_TICKS(1000));
  i2c_cmd_link_delete(link);

  if (!(status & 0x08)) {
    send_cmd(0xBE, 0x08, 0x00);
    vTaskDelay(pdMS_TO_TICKS(10));
  }
  return ESP_OK;
}

esp_err_t drv_aht20_read(float *temperature, float *humidity) {
  send_cmd(0xAC, 0x33, 0x00);    // Trigger
  vTaskDelay(pdMS_TO_TICKS(80)); // Wait measurement

  uint8_t data[6];
  i2c_cmd_handle_t link = i2c_cmd_link_create();
  i2c_master_start(link);
  i2c_master_write_byte(link, (AHT20_ADDR << 1) | I2C_MASTER_READ, true);
  i2c_master_read_byte(link, &data[0],
                       I2C_MASTER_ACK); // Status (skip logic check for brevity)
  i2c_master_read(link, &data[0], 5, I2C_MASTER_ACK); // Read 5 bytes data
  i2c_master_read_byte(link, &data[5],
                       I2C_MASTER_NACK); // Last byte (CRC) -> NACK
  i2c_master_stop(link);

  esp_err_t ret = i2c_master_cmd_begin(I2C_PORT, link, pdMS_TO_TICKS(1000));
  i2c_cmd_link_delete(link);

  if (ret != ESP_OK)
    return ret;

  uint32_t h_raw =
      ((uint32_t)data[0] << 12) | ((uint32_t)data[1] << 4) | (data[2] >> 4);
  uint32_t t_raw =
      (((uint32_t)data[2] & 0x0F) << 16) | ((uint32_t)data[3] << 8) | data[4];

  *humidity = ((float)h_raw / 1048576.0f) * 100.0f;
  *temperature = ((float)t_raw / 1048576.0f) * 200.0f - 50.0f;
  return ESP_OK;
}