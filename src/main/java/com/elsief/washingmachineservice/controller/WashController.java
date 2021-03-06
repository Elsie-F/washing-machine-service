package com.elsief.washingmachineservice.controller;

import com.elsief.washingmachineservice.api.CreateWashRequest;
import com.elsief.washingmachineservice.api.CreateWashResponse;
import com.elsief.washingmachineservice.api.CancelWashResponse;
import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.repository.WashRepo;
import com.elsief.washingmachineservice.service.*;
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
    private final ScheduleService scheduleService;

    @Autowired
    public WashController(WashRepo washRepo, WashService washService, ApplianceService applianceService, ProgramService programService, ScheduleService scheduleService) {
        this.washRepo = washRepo;
        this.washService = washService;
        this.applianceService = applianceService;
        this.programService = programService;
        this.scheduleService = scheduleService;
    }

    /**
     * Get all washes
     * @return a list of washes
     */
    @GetMapping("/all")
    public List<Wash> findAll() {
        return washRepo.findAll();
    }

    /**
     * Create a new wash and schedule its start and finish
     * @param request - request to create wash
     * @return response with wash id, HTTP status and message
     */
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
                .status(WashStatus.WAITING)
                .build());

        scheduleService.scheduleStart(createdWash);
        scheduleService.scheduleFinish(createdWash);
        return CreateWashResponse.builder().washId(createdWash.getId()).httpStatus(HttpStatus.CREATED).message("Wash successfully created").build();
    }

    /**
     * Cancel wash
     * @param washId - id of a wash to be canceled
     * @return response with wash id, HTTP status and message
     */
    @PostMapping("/cancel/{washId}")
    public CancelWashResponse cancelWash(@PathVariable long washId) {
        Optional<Wash> wash = washService.findById(washId);
        if (wash.isEmpty()) {
            return CancelWashResponse.builder().httpStatus(HttpStatus.NOT_FOUND).message("Wash with id " + washId + " not found").build();
        }
        scheduleService.cancelWash(wash.get().getId());
        return CancelWashResponse.builder().washId(washId).httpStatus(HttpStatus.OK).message("Wash with id " + washId + " canceled").build();
    }

}
