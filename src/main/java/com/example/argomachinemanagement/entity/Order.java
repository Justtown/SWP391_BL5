package com.example.argomachinemanagement.entity;

import lombok.*;

import java.sql.Date;

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
    private Integer machineId;
    private String serviceDescription;
    private Date startDate;
    private Date endDate;
    private String status;
    private Double totalCost;
    private Integer createdBy;
}