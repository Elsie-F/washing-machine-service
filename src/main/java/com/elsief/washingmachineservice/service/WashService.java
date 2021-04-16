package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.repository.WashRepo;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WashService {
    private final WashRepo washRepo;
    private final WashExecutor washExecutor;

    @Autowired
    public WashService(WashRepo washRepo, WashExecutor washExecutor) {
        this.washRepo = washRepo;
        this.washExecutor = washExecutor;
    }

    public Wash create(Wash wash) {
        Wash createdWash = washRepo.save(wash);
        washExecutor.startWashing(createdWash);
        return createdWash;
    }

    public Wash setWashStatus(long washId, WashStatus status) throws NotFoundException {
        Optional<Wash> wash = washRepo.findById(washId);
        if (wash.isEmpty()) {
            throw new NotFoundException("Wash with id " + washId + " not found");
        }
        wash.get().setStatus(status);
        return washRepo.save(wash.get());
    }

    public Wash setWashFinishTime(long washId, LocalDateTime finishTime) throws NotFoundException {
        Optional<Wash> wash = washRepo.findById(washId);
        if (wash.isEmpty()) {
            throw new NotFoundException("Wash with id " + washId + " not found");
        }
        wash.get().setFinishTime(finishTime);
        return washRepo.save(wash.get());
    }
}
