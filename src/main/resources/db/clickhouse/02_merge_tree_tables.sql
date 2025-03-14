-- Tạo bảng MergeTree cho device data
CREATE TABLE IF NOT EXISTS default.device_data (
    id UInt64,
    device_id String,
    timestamp DateTime64(3),
    sensor_type String,
    value Float64,
    unit String,
    status String DEFAULT 'ACTIVE',
    created_at DateTime64(3) DEFAULT now64(3),
    insert_time DateTime64(3) DEFAULT now64(3)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (device_id, timestamp);

-- Tạo bảng MergeTree cho device alarms
CREATE TABLE IF NOT EXISTS default.device_alarms (
    id UInt64,
    device_id String,
    triggered_at DateTime64(3),
    alarm_type String,
    severity String,
    message String,
    status String DEFAULT 'ACTIVE',
    created_at DateTime64(3) DEFAULT now64(3),
    insert_time DateTime64(3) DEFAULT now64(3)
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(triggered_at)
ORDER BY (device_id, triggered_at);