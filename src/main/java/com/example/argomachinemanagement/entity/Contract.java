package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Entity Contract theo bảng contracts trong database
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    private Integer id;
    private String contractCode;
    private Integer customerId;
    private Integer managerId;
    private Integer saleId; // ID của sale tạo contract
    private Date startDate;
    private Date endDate;
    private String status; // DRAFT, ACTIVE, FINISHED, CANCELLED
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Thông tin từ bảng users (để hiển thị)
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String managerName;
    
    // Danh sách máy trong hợp đồng
    private List<ContractItem> items;
}

