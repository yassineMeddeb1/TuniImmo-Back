package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class TopPropertyDTO {
    private Long id;
    private String title;
    private Integer views;  // Changed from int to Integer
    private Long reservations;  // Changed from int to long

    public TopPropertyDTO(Long id, String title, Integer views, Long reservations) {
        this.id = id;
        this.title = title;
        this.views = views != null ? views : 0;
        this.reservations = reservations != null ? reservations : 0L;
    }
}