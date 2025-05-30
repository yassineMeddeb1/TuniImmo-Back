package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private int totalProperties;
    private int monthlyReservations;
    private int activeReservations;
    private double occupancyRate;
    private String subscriptionType;
    private String subscriptionStartDate;
    private String subscriptionEndDate;
    private int remainingAnnouncements;
    private int announcementQuota;
    private String subscriptionStatus;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<PropertyDistributionDTO> propertyDistribution;
    private List<TopClientDTO> topClients;
    private List<UpcomingReservationDTO> upcomingReservations;
    private List<TopPropertyDTO> topProperties;


}
