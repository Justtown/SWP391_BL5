package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity Maintenance - Quản lý bảo trì máy trong kho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {
    private Integer id;
    private Integer machineId;
    private String maintenanceType;       // Loại bảo trì
    private Date maintenanceDate;         // Ngày thực hiện
    private String performedBy;           // Người thực hiện
    private String description;           // Mô tả công việc
    private String status;                // PENDING, COMPLETED, CANCELLED
    private Timestamp createdAt;
    
    // Display fields (từ JOIN với machines)
    private String machineCode;
    private String machineName;
    private String machineTypeName;
    
    /**
     * Danh sách loại bảo trì cố định
     */
    public static final String[] MAINTENANCE_TYPES = {
        "Bảo trì định kỳ",
        "Sửa chữa",
        "Thay phụ tùng",
        "Kiểm tra",
        "Vệ sinh",
        "Bảo dưỡng động cơ"
    };
}



