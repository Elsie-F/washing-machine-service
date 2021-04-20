package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.repository.ApplianceRepo;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplianceService {
    private final ApplianceRepo applianceRepo;

    @Autowired
    public ApplianceService(ApplianceRepo applianceRepo) {
        this.applianceRepo = applianceRepo;
    }

    public Optional<Appliance> findById(long applianceId) {
        return applianceRepo.findById(applianceId);
    }

    public Appliance setApplianceStatus(long applianceId, ApplianceStatus status) throws NotFoundException {
        Optional<Appliance> appliance = applianceRepo.findById(applianceId);
        if (appliance.isEmpty()) {
            throw new NotFoundException("Appliance with id " + applianceId + " not found");
        }
        appliance.get().setStatus(status);
        return applianceRepo.save(appliance.get());
    }

    public ApplianceStatus getApplianceStatus(long applianceId) throws NotFoundException {
        Optional<Appliance> appliance = applianceRepo.findById(applianceId);
        if (appliance.isEmpty()) {
            throw new NotFoundException("Wash with id " + applianceId + " not found");
        }
        return appliance.get().getStatus();
    }
}
