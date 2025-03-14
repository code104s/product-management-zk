-- Tạo Materialized View cho device data
CREATE MATERIALIZED VIEW IF NOT EXISTS default.mv_device_data TO default.device_data AS
SELECT
    cityHash64(device_id, toString(timestamp)) AS id,
    device_id,
    toDateTime64(parseDateTimeBestEffort(timestamp), 3) as timestamp,
    sensor_type,
    value,
    unit,
    status,
    toDateTime64(parseDateTimeBestEffort(created_at), 3) as created_at
FROM default.kafka_device_data;

-- Tạo Materialized View cho device alarms
CREATE MATERIALIZED VIEW IF NOT EXISTS default.mv_device_alarms TO default.device_alarms AS
SELECT
    cityHash64(device_id, toString(triggered_at)) AS id,
    device_id,
    toDateTime64(parseDateTimeBestEffort(triggered_at), 3) as triggered_at,
    alarm_type,
    severity,
    message,
    status,
    toDateTime64(parseDateTimeBestEffort(created_at), 3) as created_at
FROM default.kafka_device_alarms; 