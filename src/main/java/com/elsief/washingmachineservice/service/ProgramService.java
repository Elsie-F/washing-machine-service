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

    /**
     * Find a program by id
     * @param programId - id of a program to be found
     * @return program or Optional.empty if program is not found
     */
    public Optional<Program> findById(long programId) {
        return programRepo.findById(programId);
    }
}
