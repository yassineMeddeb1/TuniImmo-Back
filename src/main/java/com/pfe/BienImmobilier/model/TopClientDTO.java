package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopClientDTO {
    private Long id;
    private String name;
    private String initials;
    private long reservations;
}
