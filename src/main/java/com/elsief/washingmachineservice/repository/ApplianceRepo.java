package com.elsief.washingmachineservice.repository;

import com.elsief.washingmachineservice.entity.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepo extends JpaRepository<Appliance, Long> {
}
