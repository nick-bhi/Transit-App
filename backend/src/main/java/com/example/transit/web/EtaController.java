package com.example.transit.web;

import com.example.transit.service.EtaService;
import com.example.transit.service.GtfsDataLoader;
import com.example.transit.repository.StopTimeRepository;
import com.example.transit.repository.ArrivalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api")
public class EtaController {
    private final EtaService etaService;
    private final GtfsDataLoader gtfsDataLoader;
    private final StopTimeRepository stopTimeRepository;
    private final ArrivalRepository arrivalRepository;
    
    public EtaController(EtaService etaService, GtfsDataLoader gtfsDataLoader, StopTimeRepository stopTimeRepository, ArrivalRepository arrivalRepository) {
        this.etaService = etaService;
        this.gtfsDataLoader = gtfsDataLoader;
        this.stopTimeRepository = stopTimeRepository;
        this.arrivalRepository = arrivalRepository;
    }

    @GetMapping("/eta")
    public List<EtaService.Eta> eta(@RequestParam String stopId, @RequestParam String routeId){
        return etaService.getEtas(stopId, routeId);
    }
    
    @GetMapping("/eta/test")
    public List<EtaService.Eta> etaTest(@RequestParam String stopId, @RequestParam String routeId){
        // Return mock data for testing
        return List.of(
            new EtaService.Eta(stopId, routeId, System.currentTimeMillis() + 300000, 120), // 5 min from now, 2 min delay
            new EtaService.Eta(stopId, routeId, System.currentTimeMillis() + 600000, 0)   // 10 min from now, on time
        );
    }
    
    @PostMapping("/load-data")
    public String loadGtfsData() {
        try {
            gtfsDataLoader.loadStaticData();
            
            // Check database counts
            long stopCount = gtfsDataLoader.getStopRepository().count();
            long routeCount = gtfsDataLoader.getRouteRepository().count();
            long stopTimeCount = stopTimeRepository.count();
            long arrivalCount = arrivalRepository.count();
            
            return String.format("GTFS data loaded successfully! Stops: %d, Routes: %d, Stop Times: %d, Arrivals: %d", stopCount, routeCount, stopTimeCount, arrivalCount);
        } catch (Exception e) {
            return "Error loading GTFS data: " + e.getMessage();
        }
    }
    
    @GetMapping("/arrivals")
    public String getArrivalsCount() {
        long arrivalCount = arrivalRepository.count();
        return "Total arrivals in database: " + arrivalCount;
    }
}
