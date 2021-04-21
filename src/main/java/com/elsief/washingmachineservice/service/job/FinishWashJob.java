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

import java.time.LocalDateTime;

public class FinishWashJob implements Job {
    private static final Logger log = Logger.getLogger(FinishWashJob.class);

    @Setter
    private long washId;
    @Setter
    private long applianceId;
    @Setter
    private boolean cancelingWash;

    @Setter
    private ApplianceService applianceService;
    @Setter
    private WashService washService;

    public FinishWashJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            WashStatus washStatus = washService.getWashStatus(washId);
            if (cancelingWash && !WashStatus.CANCELED.equals(washStatus) && !WashStatus.FINISHED.equals(washStatus)) {
                if (WashStatus.RUNNING.equals(washStatus)) {
                    washService.setWashFinishTime(washId, LocalDateTime.now());
                    applianceService.setApplianceStatus(applianceId, ApplianceStatus.IDLE);
                }
                washService.setWashStatus(washId, WashStatus.CANCELED);
                log.info("Wash with id " + washId + " canceled");

            } else {
                if (WashStatus.RUNNING.equals(washStatus)) {
                    try {
                        washService.setWashFinishTime(washId, LocalDateTime.now());
                        washService.setWashStatus(washId, WashStatus.FINISHED);
                        log.info("Wash with id " + washId + " finished");

                        applianceService.setApplianceStatus(applianceId, ApplianceStatus.IDLE);

                    } catch (NotFoundException e) {
                        log.error("Could not perform update while finishing wash with id " + washId, e);
                    }
                }
            }
        } catch (NotFoundException e) {
            log.error("Could not perform update while finishing wash with id " + washId, e);
        }
    }
}
