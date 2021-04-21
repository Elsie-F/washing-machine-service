package com.elsief.washingmachineservice.controller;

import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.repository.ProgramRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/programs")
public class ProgramController {

    private final ProgramRepo programRepo;

    @Autowired
    public ProgramController(ProgramRepo programRepo) {
        this.programRepo = programRepo;
    }

    /**
     * Get all programs
     * @return a list of programs
     */
    @GetMapping("/all")
    public List<Program> findAll() {
        return programRepo.findAll();
    }
}
