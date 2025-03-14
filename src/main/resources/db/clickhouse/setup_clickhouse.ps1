# Di chuyển đến thư mục chứa script
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -Path $scriptPath

# Xóa các bảng cũ (nếu cần)
Write-Host "Xóa các bảng cũ..." -ForegroundColor Cyan
Get-Content 00_cleanup.sql | docker-compose exec -T clickhouse clickhouse-client --multiquery

# Tạo các bảng Kafka Engine - chạy từng lệnh riêng để tránh lỗi
Write-Host "Tạo các bảng Kafka Engine..." -ForegroundColor Cyan
Get-Content 01_kafka_tables.sql | docker-compose exec -T clickhouse clickhouse-client

# Tạo các bảng MergeTree - chạy từng lệnh riêng để tránh lỗi
Write-Host "Tạo các bảng MergeTree..." -ForegroundColor Cyan
Get-Content 02_merge_tree_tables.sql | docker-compose exec -T clickhouse clickhouse-client

# Tạo các Materialized Views - chạy từng lệnh riêng để tránh lỗi
Write-Host "Tạo các Materialized Views..." -ForegroundColor Cyan
Get-Content 03_materialized_views.sql | docker-compose exec -T clickhouse clickhouse-client

# Tạo các bảng cho Kafka Connect
Write-Host "Tạo các bảng cho Kafka Connect..." -ForegroundColor Cyan
Get-Content 04_kafka_connect_tables.sql | docker-compose exec -T clickhouse clickhouse-client

Write-Host "Hoàn thành cài đặt ClickHouse!" -ForegroundColor Green

# Kiểm tra các bảng đã tạo
Write-Host "Danh sách các bảng:" -ForegroundColor Yellow
docker-compose exec clickhouse clickhouse-client --query "SHOW TABLES" 