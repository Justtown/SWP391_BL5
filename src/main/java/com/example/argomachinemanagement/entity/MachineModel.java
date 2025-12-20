package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

/**
 * Entity MachineModel theo bảng machine_models trong database
 * Đại diện cho một dòng máy cụ thể (VD: Kubota L3408, John Deere 5055E)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineModel {
    private Integer id;
    private String modelCode;      // Mã dòng máy (unique)
    private String modelName;      // Tên dòng máy
    private String brand;          // Thương hiệu (Kubota, John Deere, Yanmar, etc.)
    private Integer typeId;        // FK -> machine_types
    private String specs;          // JSON string chứa thông số kỹ thuật
    private Timestamp createdAt;

    // Display field từ JOIN với machine_types
    private String typeName;
}
