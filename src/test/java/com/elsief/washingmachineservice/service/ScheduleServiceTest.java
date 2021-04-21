package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.service.job.FinishWashJob;
import com.elsief.washingmachineservice.service.job.StartWashJob;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {
    @Mock
    private ApplianceService mockApplianceService;
    @Mock
    private WashService mockWashService;
    @Mock
    private Scheduler mockScheduler;

    @InjectMocks
    private ScheduleService scheduleService;

    private Wash wash;
    private Appliance appliance;
    private Program program;
    private JobDetail startJob;
    private JobDetail finishJob;
    private Trigger startTrigger;
    private Trigger finishTrigger;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(scheduleService, "scheduler", mockScheduler);

        appliance = Appliance.builder().id(1).name("someName").status(ApplianceStatus.IDLE).build();
        program = Program.builder().id(1).name("someName").durationInMinutes(10).temperature(60).spinSpeed(800).build();
        wash = Wash.builder()
                .id(1)
                .appliance(appliance)
                .program(program)
                .startTime(LocalDateTime.parse("2021-04-21 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(WashStatus.WAITING)
                .build();

        startJob = newJob(StartWashJob.class)
                .withIdentity("wash1", "start")
                .usingJobData("washId", 1)
                .usingJobData("applianceId", 1)
                .usingJobData(new JobDataMap(ImmutableMap.of("applianceService", mockApplianceService, "washService", mockWashService)))
                .build();
        startTrigger = newTrigger()
                .withIdentity("washTrigger1", "start")
                .startAt(Timestamp.valueOf("2021-04-21 15:00:00"))
                .build();

        finishJob = newJob(FinishWashJob.class)
                .withIdentity("wash1", "finish")
                .usingJobData("washId", 1)
                .usingJobData("applianceId", 1)
                .usingJobData("cancelingWash", false)
                .usingJobData(new JobDataMap(ImmutableMap.of("applianceService", mockApplianceService, "washService", mockWashService)))
                .build();
        finishTrigger = newTrigger()
                .withIdentity("washTrigger1", "finish")
                .startAt(Timestamp.valueOf("2021-04-21 15:10:00"))
                .build();

    }

    @Test
    public void testScheduleStart() throws SchedulerException {

        when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(Timestamp.valueOf("2021-04-21 15:00:00"));

        scheduleService.scheduleStart(wash);

        verify(mockScheduler).scheduleJob(startJob, startTrigger);
        verifyNoMoreInteractions(mockScheduler);
    }

    @Test
    public void testScheduleFinish() throws SchedulerException {

        when(mockScheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(Timestamp.valueOf("2021-04-21 15:10:00"));

        scheduleService.scheduleFinish(wash);

        verify(mockScheduler).scheduleJob(finishJob, finishTrigger);
        verifyNoMoreInteractions(mockScheduler);
    }

    @Test
    public void testCancelWash() throws SchedulerException {

        JobDetail newJob = newJob(FinishWashJob.class)
                .withIdentity("wash1", "finish")
                .usingJobData("washId", 1)
                .usingJobData("applianceId", 1)
                .usingJobData("cancelingWash", true)
                .usingJobData(new JobDataMap(ImmutableMap.of("applianceService", mockApplianceService, "washService", mockWashService)))
                .build();

        Trigger newTrigger = newTrigger()
                .withIdentity("washTrigger1", "finish")
                .startAt(Timestamp.valueOf("2021-04-21 15:10:00"))
                .build();

        when(mockScheduler.getJobDetail(any(JobKey.class))).thenReturn(finishJob);
        when(mockScheduler.getTrigger(any(TriggerKey.class))).thenReturn(finishTrigger);
        when(mockScheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class))).thenReturn(Timestamp.valueOf("2021-04-21 15:10:00"));

        scheduleService.cancelWash(1L);

        InOrder order = inOrder(mockScheduler);
        order.verify(mockScheduler).deleteJob(startJob.getKey());
        order.verify(mockScheduler).getJobDetail(finishJob.getKey());
        order.verify(mockScheduler).addJob(newJob, true, true);
        order.verify(mockScheduler).getTrigger(finishTrigger.getKey());
        order.verify(mockScheduler).rescheduleJob(newTrigger.getKey(), newTrigger);
        verifyNoMoreInteractions(mockScheduler);
    }

}
