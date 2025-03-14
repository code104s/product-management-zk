-- Xóa các Materialized Views
DROP VIEW IF EXISTS default.mv_device_data;
DROP VIEW IF EXISTS default.mv_device_alarms;

-- Xóa các bảng dữ liệu
DROP TABLE IF EXISTS default.device_data;
DROP TABLE IF EXISTS default.device_alarms;

-- Xóa các bảng Kafka Engine
DROP TABLE IF EXISTS default.kafka_device_data;
DROP TABLE IF EXISTS default.kafka_device_alarms;

-- Xóa các bảng cho Kafka Connect
DROP TABLE IF EXISTS default.`device-data-topic`;
DROP TABLE IF EXISTS default.`device-alarm-topic`; 