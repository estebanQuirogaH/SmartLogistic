package com.projectStore.controller;

import com.projectStore.service.GeolocationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("geolocation")
public class GeolocationController {

    private final GeolocationService geolocationService;

    public GeolocationController(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    @GetMapping("/coordinates")
    public String getCoordinates(@RequestParam String address) {
        return geolocationService.getCoordinates(address);
    }
}
