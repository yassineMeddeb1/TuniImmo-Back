// DashboardController.java
package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.DashboardDTO;
import com.pfe.BienImmobilier.services.impl.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/owner/{proprietaireId}")
    public DashboardDTO getOwnerDashboard(@PathVariable Long proprietaireId) {
        return dashboardService.getOwnerDashboardData(proprietaireId);
    }
}