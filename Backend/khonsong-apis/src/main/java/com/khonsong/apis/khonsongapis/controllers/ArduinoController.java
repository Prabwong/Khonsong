package com.khonsong.apis.khonsongapis.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.khonsong.apis.khonsongapis.DeliverRoute;
import com.khonsong.apis.khonsongapis.Route;
import com.khonsong.apis.khonsongapis.Services.DeliverRouteService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping("/arduino")
public class ArduinoController {

    @Autowired
    private DeliverRouteService deliverRouteService;

    // * Get Deliver Route info */
    @GetMapping("/getInput")
    public String getMethodName() {
        String responseData = deliverRouteService.checkInput();
        if (responseData == null) {
            return "no";
        }
        // System.out.println("I get request from arduino");
        return responseData;
    }

    @PostMapping("/carStatus")
    public Map<String, Object> postMethodName(@RequestBody Route entity) {
        // TODO: process POST request
        Map<String, Object> responseData = new HashMap<>();
        // System.out.println(entity.getRouteID());
        responseData.put("data", "I got ur message!");
        return responseData;
    }

    @PostMapping("/restart")
    public String checkRestart(@RequestBody DeliverRoute deliver) {
        String response = "";
        if (deliver != null) {
            response = deliverRouteService.setRestart(true);
        } else {
            response += "no";
        }
        return response;
    }

}
