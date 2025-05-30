package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.StatisticsDTO;
import com.pfe.BienImmobilier.services.impl.StatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;



    @GetMapping
    public ResponseEntity<StatisticsDTO> getAdminStatistics() {
        return ResponseEntity.ok(statisticsService.getAdminStatistics());
    }
}