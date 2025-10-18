package com.example.transit.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RealTimePoller {
    private final EtaService etaService;
    
    public RealTimePoller(EtaService etaService) {
        this.etaService = etaService;
    }
    
    @Scheduled(fixedDelayString = "${poll.intervalMs:20000}")
    public void tick(){ 
        etaService.refresh(); 
    }
}
