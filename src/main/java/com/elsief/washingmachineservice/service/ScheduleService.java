package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.service.job.FinishWashJob;
import com.elsief.washingmachineservice.service.job.StartWashJob;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Timestamp;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@Service
public class ScheduleService {
    private static final Logger log = Logger.getLogger(ScheduleService.class);
    private Scheduler scheduler;

    @Autowired
    private ApplianceService applianceService;
    @Autowired
    private WashService washService;

    @PostConstruct
    public void init() throws SchedulerException {
        this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
    }

    @PreDestroy
    public void destroy() throws SchedulerException {
        scheduler.shutdown();
    }

    public void scheduleStart(Wash wash) {
        long applianceId = wash.getAppliance().getId();
        long washId = wash.getId();
        JobDetail job = newJob(StartWashJob.class)
                .withIdentity("wash" + washId, "start")
                .usingJobData("washId", washId)
                .usingJobData("applianceId", applianceId)
                .usingJobData(new JobDataMap(ImmutableMap.of("applianceService", applianceService, "washService", washService)))
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("washTrigger" + washId, "start")
                .startAt(Timestamp.valueOf(wash.getStartTime()))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Could not schedule start job for wash with id " + washId, e);
        }

    }

    public void scheduleFinish(Wash wash) {
        long applianceId = wash.getAppliance().getId();
        long washId = wash.getId();
        JobDetail job = newJob(FinishWashJob.class)
                .withIdentity("wash" + washId, "finish")
                .usingJobData("washId", washId)
                .usingJobData("applianceId", applianceId)
                .usingJobData("cancelingWash", false)
                .usingJobData(new JobDataMap(ImmutableMap.of("applianceService", applianceService, "washService", washService)))
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("washTrigger" + washId, "finish")
                .startAt(Timestamp.valueOf(wash.getStartTime().plusMinutes(wash.getProgram().getDurationInMinutes())))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Could not schedule finish job for wash with id " + washId, e);
        }

    }

    public void cancelWash(Wash wash) {
        long washId = wash.getId();
        try {
            scheduler.deleteJob(JobKey.jobKey("wash" + washId, "start"));
            JobDetail oldJob = scheduler.getJobDetail(JobKey.jobKey("wash" + washId, "finish"));
            JobBuilder jb = oldJob.getJobBuilder();
            JobDetail newJob = jb.usingJobData("cancelingWash", true).build();
            scheduler.addJob(newJob, true);

            Trigger oldTrigger = scheduler.getTrigger(triggerKey("washTrigger" + washId, "finish"));
            TriggerBuilder tb = oldTrigger.getTriggerBuilder();
            Trigger newTrigger = tb.startNow().build();
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        } catch (SchedulerException e) {
            log.error("Error while canceling wash with id " + washId, e);
        }

    }

}
