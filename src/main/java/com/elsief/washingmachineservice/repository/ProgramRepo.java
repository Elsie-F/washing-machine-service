package com.elsief.washingmachineservice.repository;

import com.elsief.washingmachineservice.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepo extends JpaRepository<Program, Long> {
}
