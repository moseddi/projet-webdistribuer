package com.example.commandeservice.controller;

import com.example.commandeservice.dto.CommandeStatsDTO;
import com.example.commandeservice.service.CommandeStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commandes/stats")
public class CommandeStatsController {

    @Autowired
    private CommandeStatsService commandeStatsService;

    @GetMapping
    public ResponseEntity<CommandeStatsDTO> getStats() {
        return ResponseEntity.ok(commandeStatsService.getStats());
    }
}
