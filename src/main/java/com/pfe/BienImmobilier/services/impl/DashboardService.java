package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final AbonnementRepository abonnementRepository;
    private final ReservationRepository reservationRepository;

    public DashboardService(DashboardRepository dashboardRepository,
                            AbonnementRepository abonnementRepository,
                            ReservationRepository reservationRepository) {
        this.dashboardRepository = dashboardRepository;
        this.abonnementRepository = abonnementRepository;
        this.reservationRepository = reservationRepository;
    }

    public DashboardDTO getOwnerDashboardData(Long proprietaireId) {
        DashboardDTO dashboardDTO = new DashboardDTO();

        // Basic stats
        dashboardDTO.setTotalProperties(dashboardRepository.countPropertiesByProprietaire(proprietaireId));
        dashboardDTO.setMonthlyReservations(dashboardRepository.countMonthlyReservationsByProprietaire(proprietaireId));
        dashboardDTO.setActiveReservations(dashboardRepository.countActiveReservationsByProprietaire(proprietaireId));

        // Calculate occupancy rate
        Double avgOccupancy = dashboardRepository.calculateAverageOccupancyRate(proprietaireId);
        dashboardDTO.setOccupancyRate(avgOccupancy != null ? Math.min(100, avgOccupancy / 30 * 100) : 0);

        // Subscription info
        Optional<Abonnement> activeAbonnement = abonnementRepository.findByUtilisateurIdAndStatus(proprietaireId, AbonnementStatus.ACTIVE);
        Abonnement abonnement = activeAbonnement.orElseGet(() ->
                abonnementRepository.findByUtilisateurIdOrderByDateFinDesc(proprietaireId)
                        .stream()
                        .findFirst()
                        .orElse(null)
        );

        if (abonnement != null) {
            dashboardDTO.setSubscriptionType(abonnement.getType() != null ? abonnement.getType().name() : "N/A");
            dashboardDTO.setSubscriptionStartDate(abonnement.getDateDebut() != null ?
                    abonnement.getDateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE) : "-");
            dashboardDTO.setSubscriptionEndDate(abonnement.getDateFin() != null ?
                    abonnement.getDateFin().format(DateTimeFormatter.ISO_LOCAL_DATE) : "-");
            dashboardDTO.setRemainingAnnouncements(abonnement.getAnnoncesRestantes() != null ?
                    abonnement.getAnnoncesRestantes() : 0);
            dashboardDTO.setAnnouncementQuota(abonnement.getType() != null ?
                    abonnement.getType().getMaxAnnonces() : 0);
            dashboardDTO.setSubscriptionStatus(abonnement.getStatus() != null ?
                    abonnement.getStatus().name() : "INACTIVE");
        } else {
            dashboardDTO.setSubscriptionType("Aucun");
            dashboardDTO.setSubscriptionStartDate("-");
            dashboardDTO.setSubscriptionEndDate("-");
            dashboardDTO.setRemainingAnnouncements(0);
            dashboardDTO.setAnnouncementQuota(0);
            dashboardDTO.setSubscriptionStatus("INACTIVE");
        }

        // Charts data
        dashboardDTO.setMonthlyRevenue(getMonthlyRevenue(proprietaireId));
        dashboardDTO.setPropertyDistribution(dashboardRepository.findPropertyDistributionByProprietaire(proprietaireId));
        dashboardDTO.setTopClients(dashboardRepository.findTopClientsByProprietaire(proprietaireId, PageRequest.of(0, 3)));
        dashboardDTO.setTopProperties(dashboardRepository.findTopPropertiesByProprietaire(proprietaireId, PageRequest.of(0, 3)));

        // Upcoming reservations
        List<Reservation> upcomingReservations = reservationRepository
                .findReservationsEndingInNext3Days(proprietaireId, LocalDateTime.now().plusDays(3));

        dashboardDTO.setUpcomingReservations(upcomingReservations.stream()
                .map(res -> {
                    UpcomingReservationDTO dto = new UpcomingReservationDTO();
                    dto.setId(res.getId());
                    dto.setBienTitle(res.getBienImmobilier().getTitre());
                    dto.setClientName(res.getUtilisateur().getPrenom() + " " + res.getUtilisateur().getNom());
                    dto.setDateDebut(res.getDateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    dto.setDateFin(res.getDateFin().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    dto.setDaysLeft((int) ChronoUnit.DAYS.between(LocalDateTime.now(), res.getDateFin()));
                    return dto;
                })
                .collect(Collectors.toList()));


        return dashboardDTO;
    }
    public List<MonthlyRevenueDTO> getMonthlyRevenue(Long proprietaireId) {
        // Version JPQL
        List<MonthlyRevenueDTO> revenueData = dashboardRepository.findMonthlyRevenueForCurrentYear(proprietaireId);

        // OU version native si nécessaire
        // List<MonthlyRevenueDTO> revenueData = dashboardRepository.findMonthlyRevenueNative(proprietaireId)
        //     .stream()
        //     .map(p -> new MonthlyRevenueDTO(p.getMonth(), p.getYear(), p.getTotalRevenue()))
        //     .collect(Collectors.toList());

        // Compléter avec les mois sans réservation
        return completeMissingMonths(revenueData);
    }

    private List<MonthlyRevenueDTO> completeMissingMonths(List<MonthlyRevenueDTO> existingData) {
        List<MonthlyRevenueDTO> completeData = new ArrayList<>();
        int currentYear = Year.now().getValue();

        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%02d-%d", month, currentYear);
            Optional<MonthlyRevenueDTO> existingMonth = existingData.stream()
                    .filter(d -> d.getMonth().equals(monthKey))
                    .findFirst();

            if (existingMonth.isPresent()) {
                completeData.add(existingMonth.get());
            } else {
                completeData.add(new MonthlyRevenueDTO(month, currentYear, 0.0));
            }
        }

        return completeData;
    }
}