package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.repository.ProgramRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProgramService {
    private final ProgramRepo programRepo;

    @Autowired
    public ProgramService(ProgramRepo programRepo) {
        this.programRepo = programRepo;
    }

    public Optional<Program> findById(long programId) {
        return programRepo.findById(programId);
    }
}
