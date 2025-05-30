package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.EStatutReservation;
import com.pfe.BienImmobilier.model.StatisticsDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.ReservationRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pfe.BienImmobilier.entities.EStatutReservation.ANNULEE;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final BienImmobilierRepository bienImmobilierRepository;
    private final ReservationRepository reservationRepository;
    private final HttpServletRequest request;

    public StatisticsDTO getAdminStatistics() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        StatisticsDTO stats = new StatisticsDTO();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }

        // Utilisateurs
        stats.setTotalUsers(userRepository.count());

        // Biens immobiliers
        stats.setTotalProperties(bienImmobilierRepository.count());
        stats.setActiveProperties(bienImmobilierRepository.countByIsVerifieAdmin(1));
        stats.setPendingProperties(bienImmobilierRepository.countByIsVerifieAdmin(0));

        // Revenus
        stats.setMonthlyRevenue(calculateMonthlyRevenue());
        stats.setAnnualRevenue(calculateAnnualRevenue());

        // Taux de conversion
        stats.setConversionRate(calculateConversionRate());

        // Répartition des biens
        stats.setPropertyDistribution(getPropertyDistribution());

        // Statistiques des réservations
        stats.setReservationStats(getReservationStats());

        // Activité des utilisateurs
        stats.setUserActivity(getUserActivity());

        return stats;
    }

    private double calculateMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        return reservationRepository.sumRevenueByMonth(now.getMonthValue(), now.getYear());
    }

    private double calculateAnnualRevenue() {
        return reservationRepository.sumRevenueByYear(LocalDate.now().getYear());
    }

    private double calculateConversionRate() {
        long totalViews = bienImmobilierRepository.sumAllViews();
        long totalReservations = reservationRepository.count();
        return totalViews > 0 ? (double) totalReservations / totalViews * 100 : 0;
    }

    private Map<String, Long> getPropertyDistribution() {
        return bienImmobilierRepository.countPropertiesByCategory()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

    private Map<String, Long> getReservationStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", reservationRepository.count());
        stats.put("confirmed", reservationRepository.countByStatut(EStatutReservation.CONFIRMEE));
        stats.put("pending", reservationRepository.countByStatut(EStatutReservation.EN_ATTENTE));
        stats.put("cancelled", reservationRepository.countByStatut(EStatutReservation.ANNULEE));
        return stats;
    }

    private Map<String, Long> getUserActivity() {
        Map<String, Long> activity = new HashMap<>();
        activity.put("newUsers", userRepository.countByCreatedAtAfter());
        activity.put("activeUsers", userRepository.countActiveUsers(LocalDate.now().minusDays(30).atStartOfDay()));
        return activity;
    }
}