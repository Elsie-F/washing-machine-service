package com.elsief.washingmachineservice.repository;

import com.elsief.washingmachineservice.entity.Wash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WashRepo extends JpaRepository<Wash, Long> {
}
