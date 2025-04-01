package com.projectStore.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Document {
    private EDocument documentType;
    private String documentNumber;
}
