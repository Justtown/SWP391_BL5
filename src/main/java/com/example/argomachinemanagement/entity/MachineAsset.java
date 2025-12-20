package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity MachineAsset theo bảng machine_assets trong database
 * Đại diện cho từng chiếc máy vật lý cụ thể với serial number riêng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineAsset {
    private Integer id;
    private String serialNumber;     // Số serial (unique)
    private Integer modelId;         // FK -> machine_models
    private String status;           // ACTIVE, INACTIVE
    private String rentalStatus;     // AVAILABLE, RENTED, MAINTENANCE
    private String location;         // Vị trí hiện tại của máy
    private Date purchaseDate;       // Ngày mua
    private String note;             // Ghi chú
    private Timestamp createdAt;

    // Display fields từ JOIN với machine_models và machine_types
    private String modelCode;
    private String modelName;
    private String brand;
    private String typeName;
}
