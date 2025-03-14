-- Tạo bảng Kafka Engine cho device data processed
CREATE TABLE IF NOT EXISTS default.kafka_device_data (
    device_id String,
    timestamp String,
    sensor_type String,
    value Float64,
    unit String,
    status String,
    created_at String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'device-data-processed',
    kafka_group_name = 'clickhouse_device_data_consumer',
    kafka_format = 'JSONEachRow',
    kafka_skip_broken_messages = 100,
    kafka_num_consumers = 1,
    kafka_max_block_size = 100000,
    kafka_poll_max_batch_size = 100000,
    kafka_poll_timeout_ms = 1000;

-- Tạo bảng Kafka Engine cho device alarms processed
CREATE TABLE IF NOT EXISTS default.kafka_device_alarms (
    device_id String,
    triggered_at String,
    alarm_type String,
    severity String,
    message String,
    status String,
    created_at String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka:9092',
    kafka_topic_list = 'device-alarm-processed',
    kafka_group_name = 'clickhouse_device_alarm_consumer',
    kafka_format = 'JSONEachRow',
    kafka_skip_broken_messages = 100,
    kafka_num_consumers = 1,
    kafka_max_block_size = 100000,
    kafka_poll_max_batch_size = 100000,
    kafka_poll_timeout_ms = 1000; 