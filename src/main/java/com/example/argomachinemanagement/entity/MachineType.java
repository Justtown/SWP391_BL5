package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity MachineType theo bảng machine_types trong database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineType {
    private Integer id;
    private String typeName;
    private String description;
}

