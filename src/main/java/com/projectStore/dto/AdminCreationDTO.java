package com.projectStore.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreationDTO{

    private String name;
    private String email;
    private String password;
    private String documentType;
    private String documentNumber;
    private List<Long> assignedStoreIds;

}