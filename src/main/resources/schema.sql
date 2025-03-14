-- Tạo bảng devices
CREATE TABLE IF NOT EXISTS devices (
    id UInt64,
    device_id String,
    name String,
    type Nullable(String),
    status Nullable(String),
    location Nullable(String),
    ip_address Nullable(String),
    mac_address Nullable(String),
    firmware_version Nullable(String),
    last_online Nullable(DateTime64(3)),
    created_at DateTime64(3),
    updated_at DateTime64(3)
) ENGINE = MergeTree()
ORDER BY (device_id, created_at);

-- Tạo bảng device_data
CREATE TABLE IF NOT EXISTS device_data (
    id UInt64,
    device_id String,
    sensor_type String,
    value Float64,
    unit String,
    status String,
    timestamp DateTime64(3),
    created_at DateTime64(3)
) ENGINE = MergeTree()
ORDER BY (device_id, timestamp);

-- Tạo bảng device_alarms
CREATE TABLE IF NOT EXISTS device_alarms (
    id UInt64,
    device_id String,
    alarm_type String,
    severity String,
    message Nullable(String),
    value Nullable(Float64),
    threshold Nullable(Float64),
    status String,
    acknowledged UInt8,
    acknowledged_by Nullable(String),
    acknowledged_at Nullable(DateTime64(3)),
    triggered_at DateTime64(3),
    resolved_at Nullable(DateTime64(3))
) ENGINE = MergeTree()
ORDER BY (device_id, triggered_at);

-- Tạo view để lấy cảnh báo đang hoạt động
CREATE VIEW IF NOT EXISTS active_alarms AS
SELECT *
FROM device_alarms
WHERE status = 'ACTIVE';

-- Tạo view để lấy dữ liệu thiết bị mới nhất
CREATE VIEW IF NOT EXISTS latest_device_data AS
SELECT
    device_id,
    argMax(sensor_type, timestamp) AS sensor_type,
    argMax(value, timestamp) AS value,
    argMax(unit, timestamp) AS unit,
    argMax(status, timestamp) AS status,
    max(timestamp) AS last_updated_time
FROM device_data
GROUP BY device_id; 