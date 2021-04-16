package com.elsief.washingmachineservice.controller;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.repository.ApplianceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appliances")
public class ApplianceController {

    private final ApplianceRepo applianceRepo;

    @Autowired
    public ApplianceController(ApplianceRepo applianceRepo) {
        this.applianceRepo = applianceRepo;
    }

    @GetMapping("/all")
    public List<Appliance> findAll() {
        return applianceRepo.findAll();
    }
}
