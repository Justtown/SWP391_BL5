package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Date;

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
    private Integer assetId;
    private BigDecimal price;
    private String note;

    // Display fields (từ JOIN với machine_assets, machine_models, machine_types)
    private String serialNumber;
    private String modelCode;
    private String modelName;
    private String brand;
    private String typeName;
    private String assetStatus;
    private String rentalStatus;
    
    // Contract information (từ JOIN với contracts)
    private String contractCode;
    private String contractStatus;
    private Date contractStartDate;
    private Date contractEndDate;
}
