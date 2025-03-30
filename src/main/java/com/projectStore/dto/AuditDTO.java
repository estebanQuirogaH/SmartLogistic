package com.projectStore.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDTO {
    
    private Long id;
    private Date timestamp;
    private String ipAddress;
    private String description;
    private String userName;
    private String storeName;
    
}