package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineRentRequest {
    private int id;
    private int machineId;
    private int customerId;
    private Date startDate;
    private Date endDate;
    private String note;
    private String status; // PENDING, APPROVED, REJECTED
    private Integer reviewedBy;
    private Timestamp createdAt;
    private Timestamp reviewedAt;
}
