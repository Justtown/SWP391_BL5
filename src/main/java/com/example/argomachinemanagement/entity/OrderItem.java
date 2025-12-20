package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity đại diện cho bảng order_items
 * Chi tiết máy trong đơn hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Integer id;
    private Integer orderId;
    private Integer assetId;
    private BigDecimal price;
    private String note;

    // Display fields (from JOIN with machine_assets, machine_models, machine_types)
    private String serialNumber;
    private String modelCode;
    private String modelName;
    private String brand;
    private String typeName;
    private String assetStatus;         // ACTIVE, INACTIVE
    private String assetRentalStatus;   // AVAILABLE, RENTED, MAINTENANCE
}
