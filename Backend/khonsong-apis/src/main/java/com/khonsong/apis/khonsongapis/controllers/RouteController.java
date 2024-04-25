package com.khonsong.apis.khonsongapis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.khonsong.apis.khonsongapis.Route;
import com.khonsong.apis.khonsongapis.Model.ArduinoPublish;
import com.khonsong.apis.khonsongapis.Services.RouteService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin
@RequestMapping("/route")
public class RouteController {
    private RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ! for arduino
    // * Create route */
    @PostMapping("/create")
    public String createRoute(@RequestBody ArduinoPublish data) {
        String response = routeService.create(data);
        return response;
    }

    // * Find routes for deliver */
    @GetMapping("/routeForDeliver")
    public Map<String, Object> getMethodName(@RequestParam List<Integer> inputRoutes) {
        return routeService.getRoutesforDeliver(inputRoutes);
    }

    @PostMapping("/image")
    public void postImage(@RequestBody Route route) {
        routeService.getLatestRoute(route);
    }

    @PostMapping("/update")
    public String updateRoute(@RequestBody Route route) {
        return routeService.updateRoute(route);
    }

}
