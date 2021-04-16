package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.WashStatus;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WashExecutor {
    private static final Logger log = Logger.getLogger(WashExecutor.class);
    private static final Map<String, ScheduledExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    private final ApplianceService applianceService;
    // private final WashService washService;

    @Autowired
    public WashExecutor(ApplianceService applianceService) {
        this.applianceService = applianceService;
    }

    public void startWashing(Wash wash) {
        String applianceName = wash.getAppliance().getName();
        ScheduledExecutorService executorService = executorServiceMap.get(applianceName);
        if (executorService == null) {
            executorService = Executors.newScheduledThreadPool(1);
            executorServiceMap.put(applianceName, executorService);
        }
        executorService.schedule(createProcessRunner(wash), getDelay(wash.getStartTime()), TimeUnit.MINUTES);
    }

    public void stopWashing(Wash wash) {
        String applianceName = wash.getAppliance().getName();
        ScheduledExecutorService executorService = executorServiceMap.get(applianceName);
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private long getDelay(LocalDateTime startTime) {
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), startTime);
    }

    private Runnable createProcessRunner(Wash wash) {
        return () -> {
            if (wash.getAppliance().getStatus().equals(ApplianceStatus.IDLE)) {
                long applianceId = wash.getAppliance().getId();
                long washId = wash.getId();
                try {
                    //log.info("Preparing to wash...");
                    System.out.println("Preparing to execute wash with id " + washId);
                    applianceService.setApplianceStatus(applianceId, ApplianceStatus.RUNNING);
                    // washService.setWashStatus(washId, WashStatus.RUNNING);

                    TimeUnit.MINUTES.sleep(wash.getProgram().getDurationInMinutes());

                    // washService.setWashFinishTime(washId, LocalDateTime.now());
                    // washService.setWashStatus(washId, WashStatus.FINISHED);
                    applianceService.setApplianceStatus(applianceId, ApplianceStatus.IDLE);
                    //log.info("Wash finished");
                    System.out.println("Wash with id " + washId + " finished");

                } catch (InterruptedException e) {
                    log.error("Wash interrupted", e);
//                    try {
//                        washService.setWashStatus(washId, WashStatus.INTERRUPTED);
//                    } catch (NotFoundException ex) {
//                        log.error("Could not update wash status", ex);
//                    }
                } catch (NotFoundException e) {
                    log.error("Could not execute wash: appliance with id " + applianceId + " not found", e);
                }
            }
        };
    }

}
