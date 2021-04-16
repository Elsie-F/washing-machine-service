package com.elsief.washingmachineservice.controller;

import com.elsief.washingmachineservice.api.CreateWashRequest;
import com.elsief.washingmachineservice.api.CreateWashResponse;
import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.repository.WashRepo;
import com.elsief.washingmachineservice.service.ApplianceService;
import com.elsief.washingmachineservice.service.ProgramService;
import com.elsief.washingmachineservice.service.WashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/washes")
public class WashController {

    private final WashRepo washRepo;
    private final WashService washService;
    private final ApplianceService applianceService;
    private final ProgramService programService;

    @Autowired
    public WashController(WashRepo washRepo, WashService washService, ApplianceService applianceService, ProgramService programService) {
        this.washRepo = washRepo;
        this.washService = washService;
        this.applianceService = applianceService;
        this.programService = programService;
    }

    @GetMapping("/all")
    public List<Wash> findAll() {
        return washRepo.findAll();
    }

    @PostMapping
    public CreateWashResponse saveWash(@RequestBody CreateWashRequest request) {
        Optional<Appliance> appliance = applianceService.findById(request.getApplianceId());
        if (appliance.isEmpty()) {
            return CreateWashResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Appliance with id " + request.getApplianceId() + " does not exist").build();
        }
        Optional<Program> program = programService.findById(request.getProgramId());
        if (program.isEmpty()) {
            return CreateWashResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).message("Program with id " + request.getProgramId() + " does not exist").build();
        }
        Wash createdWash = washService.create(Wash.builder()
                .appliance(appliance.get())
                .program(program.get())
                .startTime(request.getStartTime())
                //.finishTime(request.getStartTime().plusMinutes(program.get().getDurationInMinutes()))
                .status(WashStatus.WAITING)
                .build());
        return CreateWashResponse.builder().washId(createdWash.getId()).httpStatus(HttpStatus.CREATED).message("Wash successfully created").build();
    }

}
