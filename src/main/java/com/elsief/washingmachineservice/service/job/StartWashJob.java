package com.elsief.washingmachineservice.service.job;

import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.service.ApplianceService;
import com.elsief.washingmachineservice.service.WashService;
import javassist.NotFoundException;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StartWashJob implements Job {
    private static final Logger log = Logger.getLogger(StartWashJob.class);

    @Setter
    private long washId;
    @Setter
    private long applianceId;

    @Setter
    private ApplianceService applianceService;
    @Setter
    private WashService washService;

    public StartWashJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            WashStatus washStatus = washService.getWashStatus(washId);
            if (WashStatus.WAITING.equals(washStatus)) {
                ApplianceStatus applianceStatus = applianceService.getApplianceStatus(applianceId);
                if (ApplianceStatus.IDLE.equals(applianceStatus)) {
                    try {
                        log.info("Preparing to execute wash with id " + washId);
                        applianceService.setApplianceStatus(applianceId, ApplianceStatus.RUNNING);
                        washService.setWashStatus(washId, WashStatus.RUNNING);

                    } catch (NotFoundException e) {
                        log.error("Could not perform update while starting wash with id " + washId, e);
                    }
                } else {
                    washService.setWashStatus(washId, WashStatus.CANCELED);
                    log.info("Wash with id " + washId + " canceled: appliance with id " + applianceId + " is already running");
                }
            }
        } catch (NotFoundException e) {
            log.error("Could not perform update while starting wash with id " + washId, e);
        }
    }
}
