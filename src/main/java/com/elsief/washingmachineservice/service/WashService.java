package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Wash;
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

    @Autowired
    public WashService(WashRepo washRepo) {
        this.washRepo = washRepo;
    }

    /**
     * Create a new wash
     * @param wash - wash to be created
     * @return created wash
     */
    public Wash create(Wash wash) {
        Wash createdWash = washRepo.save(wash);
        return createdWash;
    }

    /**
     * Find a wash by id
     * @param washId - id of a wash to be found
     * @return wash or Optional.empty if wash is not found
     */
    public Optional<Wash> findById(long washId) {
        return washRepo.findById(washId);
    }

    /**
     * Update wash status to a specified value
     * @param washId - id of a wash to be updated
     * @param status - new value of wash status
     * @return updated wash
     * @throws NotFoundException if wash with this id is not found
     */
    public Wash setWashStatus(long washId, WashStatus status) throws NotFoundException {
        Optional<Wash> wash = washRepo.findById(washId);
        if (wash.isEmpty()) {
            throw new NotFoundException("Wash with id " + washId + " not found");
        }
        wash.get().setStatus(status);
        return washRepo.save(wash.get());
    }

    /**
     * Update wash time of finish to a specified value
     * @param washId - id of a wash to be updated
     * @param finishTime - new value of finish time
     * @return updated wash
     * @throws NotFoundException if wash with this id is not found
     */
    public Wash setWashFinishTime(long washId, LocalDateTime finishTime) throws NotFoundException {
        Optional<Wash> wash = washRepo.findById(washId);
        if (wash.isEmpty()) {
            throw new NotFoundException("Wash with id " + washId + " not found");
        }
        wash.get().setFinishTime(finishTime);
        return washRepo.save(wash.get());
    }

    /**
     * Get status of a wash
     * @param washId - id of a wash
     * @return status of wash
     * @throws NotFoundException if wash with this id is not found
     */
    public WashStatus getWashStatus(long washId) throws NotFoundException {
        Optional<Wash> wash = washRepo.findById(washId);
        if (wash.isEmpty()) {
            throw new NotFoundException("Wash with id " + washId + " not found");
        }
        return wash.get().getStatus();
    }
}
