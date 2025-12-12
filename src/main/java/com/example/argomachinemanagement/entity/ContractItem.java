package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity ContractItem theo bảng contract_items trong database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractItem {
    private Integer id;
    private Integer contractId;
    private Integer machineId;
    private String machineNameSnapshot;
    private String note;
    
    // Thông tin từ bảng machines (để hiển thị)
    private String machineCode;
    private String machineName;
    private String machineStatus;
}

