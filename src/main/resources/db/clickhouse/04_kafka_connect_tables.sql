-- Tạo bảng cho Kafka Connect device data topic
CREATE TABLE IF NOT EXISTS default.`device-data-topic` (
    message String
) ENGINE = MergeTree()
ORDER BY tuple();

-- Tạo bảng cho Kafka Connect device alarm topic
CREATE TABLE IF NOT EXISTS default.`device-alarm-topic` (
    message String
) ENGINE = MergeTree()
ORDER BY tuple(); 