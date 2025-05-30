package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private String month; // Format "MM-YYYY"
    private Double revenue;



    // Constructeur alternatif pour requêtes natives
    public MonthlyRevenueDTO(int month, int year, double revenue) {
        this.month = String.format("%02d-%d", month, year);
        this.revenue = revenue ;
    }
}