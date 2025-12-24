# Hướng dẫn Setup Cronjob cho Contract Auto-Finish

## Mô tả
Cronjob này tự động chuyển các contract có `status = 'ACTIVE'` và `end_date < ngày hiện tại` sang `status = 'FINISHED'` và trả máy về trạng thái `AVAILABLE`.

## URL Endpoint
```
http://localhost:8080/ArgoMachineManagement/job/auto-finish-contracts
```

## Cách Setup Cronjob

### Option 1: Sử dụng Linux Cron (Khuyến nghị)

1. Mở crontab editor:
```bash
crontab -e
```

2. Thêm dòng sau để chạy mỗi ngày lúc 00:00:
```bash
0 0 * * * curl -s http://localhost:8080/ArgoMachineManagement/job/auto-finish-contracts > /dev/null 2>&1
```

Hoặc chạy mỗi giờ:
```bash
0 * * * * curl -s http://localhost:8080/ArgoMachineManagement/job/auto-finish-contracts > /dev/null 2>&1
```

3. Lưu và thoát

### Option 2: Sử dụng Windows Task Scheduler

1. Mở Task Scheduler
2. Tạo Task mới:
   - Name: Contract Auto-Finish Job
   - Trigger: Daily hoặc Hourly
   - Action: Start a program
   - Program: `curl.exe`
   - Arguments: `http://localhost:8080/ArgoMachineManagement/job/auto-finish-contracts`

### Option 3: Sử dụng Quartz Scheduler (Java)

Nếu muốn tích hợp vào ứng dụng Java, có thể sử dụng Quartz Scheduler để gọi servlet này định kỳ.

## Test Manual

Để test thủ công, có thể gọi trực tiếp:
```bash
curl http://localhost:8080/ArgoMachineManagement/job/auto-finish-contracts
```

Hoặc mở trình duyệt và truy cập URL trên.

## Logic

1. Tìm tất cả contracts có:
   - `status = 'ACTIVE'`
   - `end_date IS NOT NULL`
   - `end_date < CURDATE()` (ngày hiện tại)

2. Update status của contracts thành `'FINISHED'`

3. Update `rental_status` của tất cả `machine_assets` trong các contract đó về `'AVAILABLE'`

4. Trả về số lượng contracts đã được finish

## Logs

Job sẽ log ra console:
- Số lượng contracts tìm thấy
- ID của từng contract được finish
- ID của từng asset được set về AVAILABLE
- Tổng số contracts đã finish
