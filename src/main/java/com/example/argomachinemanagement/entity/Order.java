package com.example.argomachinemanagement.entity;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Order {
    private int id;
    private String contractCode;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private Integer machineId;  // Lưu machine_type_id
    private Integer quantity;
    private String serviceDescription;
    private Date startDate;
    private Date endDate;
    private String status;
    private Double totalCost;
    private Integer createdBy;
    private Integer approvedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String createdByName;
    private String approvedByName;
    private String machineCode;      // Mã máy từ machines
    private String machineName;      // Tên máy từ machines
    private String machineTypeName;  // Tên loại máy từ machine_types
}