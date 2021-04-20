package com.elsief.washingmachineservice.service.job;

import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.service.ApplianceService;
import com.elsief.washingmachineservice.service.WashService;
import javassist.NotFoundException;
import lombok.Setter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

public class FinishWashJob implements Job {
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
                System.out.println("Wash with id " + washId + " canceled");

            } else {
                if (WashStatus.RUNNING.equals(washStatus)) {
                    try {
                        washService.setWashFinishTime(washId, LocalDateTime.now());
                        washService.setWashStatus(washId, WashStatus.FINISHED);
                        //log.info("Wash finished");
                        System.out.println("Wash with id " + washId + " finished");

                        applianceService.setApplianceStatus(applianceId, ApplianceStatus.IDLE);

                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
