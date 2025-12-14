package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity Machine theo bảng machines trong database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Machine {
    private Integer id;
    private String machineCode;
    private String machineName;
    private Integer machineTypeId;
    private String status; // ACTIVE, INACTIVE, DISCONTINUED
    private Boolean isRentable;
    private String location;
    private Date purchaseDate;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Display field from JOIN with machine_types
    private String machineTypeName;
}

