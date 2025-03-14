#!/bin/bash

# Di chuyển đến thư mục chứa script
cd "$(dirname "$0")"

# Xóa các bảng cũ (nếu cần)
echo "Xóa các bảng cũ..."
docker-compose exec -T clickhouse clickhouse-client --multiquery < 00_cleanup.sql

# Tạo các bảng Kafka Engine
echo "Tạo các bảng Kafka Engine..."
docker-compose exec -T clickhouse clickhouse-client --multiquery < 01_kafka_tables.sql

# Tạo các bảng MergeTree
echo "Tạo các bảng MergeTree..."
docker-compose exec -T clickhouse clickhouse-client --multiquery < 02_merge_tree_tables.sql

# Tạo các Materialized Views
echo "Tạo các Materialized Views..."
docker-compose exec -T clickhouse clickhouse-client --multiquery < 03_materialized_views.sql

# Tạo các bảng cho Kafka Connect
echo "Tạo các bảng cho Kafka Connect..."
docker-compose exec -T clickhouse clickhouse-client --multiquery < 04_kafka_connect_tables.sql

echo "Hoàn thành cài đặt ClickHouse!"

# Kiểm tra các bảng đã tạo
echo "Danh sách các bảng:"
docker-compose exec clickhouse clickhouse-client --query "SHOW TABLES" 