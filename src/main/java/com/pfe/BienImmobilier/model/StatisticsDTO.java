package com.pfe.BienImmobilier.model;

import lombok.Data;

import java.util.Map;

@Data
public class StatisticsDTO {
    private long totalUsers;
    private long totalProperties;
    private long activeProperties;
    private long pendingProperties;
    private double monthlyRevenue;
    private double annualRevenue;
    private double conversionRate;
    private Map<String, Long> propertyDistribution;
    private Map<String, Long> reservationStats;
    private Map<String, Long> userActivity;
}