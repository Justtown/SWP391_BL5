package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Entity đại diện cho bảng orders
 * Đơn hàng do Sale tạo, cần Manager duyệt
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;
    private String orderCode;
    private Integer customerId;
    private Integer saleId;
    private Integer managerId;
    private Date startDate;
    private Date endDate;
    private String status;          // PENDING, APPROVED, REJECTED, CONVERTED
    private String rejectReason;
    private Integer contractId;
    private String note;
    private Timestamp createdAt;
    private Timestamp approvedAt;

    // Display fields (from JOIN with users)
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String saleName;
    private String managerName;
    private String contractCode;

    // Order items
    private List<OrderItem> items;
}
