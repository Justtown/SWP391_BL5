package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private int id;
    private String title;
    private String description;
    // PENDING, APPROVED, DECLINED
    private String status;
    private String feedback;
    private int customerId;
    private Integer salerId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String customerUsername;

}
