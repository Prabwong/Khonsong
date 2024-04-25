package com.khonsong.apis.khonsongapis.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.khonsong.apis.khonsongapis.Services.DeliverRouteService;
import com.khonsong.apis.khonsongapis.Services.StaffService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping("/staff")
public class StaffController {

    private StaffService staffService;

    public StaffController(StaffService staffService, DeliverRouteService deliverRouteService) {
        this.staffService = staffService;
    }

    // Get Name of Staff
    @GetMapping("/name")
    public String getStaffName(@RequestParam Integer staffID) {
        return staffService.getStaffNameById(staffID);
    }

}
