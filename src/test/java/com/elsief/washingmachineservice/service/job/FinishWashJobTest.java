package com.elsief.washingmachineservice.service.job;

import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.service.ApplianceService;
import com.elsief.washingmachineservice.service.WashService;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(DataProviderRunner.class)
public class FinishWashJobTest {
    @Mock
    private ApplianceService mockApplianceService;
    @Mock
    private WashService mockWashService;
    @Mock
    private JobExecutionContext mockContext;

    private FinishWashJob job;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        job = new FinishWashJob();
        job.setWashId(1L);
        job.setApplianceId(2L);
        job.setCancelingWash(false);
        job.setApplianceService(mockApplianceService);
        job.setWashService(mockWashService);
    }

    @DataProvider
    public static Object[][] provideData_noNeedToFinish() {
        return new Object[][] {
                {WashStatus.WAITING, false},
                {WashStatus.FINISHED, false},
                {WashStatus.CANCELED, false},
                {WashStatus.FINISHED, true},
                {WashStatus.CANCELED, true}
        };
    }

    @Test
    @UseDataProvider("provideData_noNeedToFinish")
    public void testExecute_noNeedToFinish(WashStatus washStatus, boolean cancelingWash) throws NotFoundException, JobExecutionException {
        job.setCancelingWash(cancelingWash);
        when(mockWashService.getWashStatus(anyLong())).thenReturn(washStatus);

        job.execute(mockContext);

        verify(mockWashService).getWashStatus(1L);
        verifyNoMoreInteractions(mockWashService);
        verifyNoInteractions(mockApplianceService);
    }

    @Test
    public void testExecute_needToFinish_washRunning_notCancelingWash() throws NotFoundException, JobExecutionException {
        when(mockWashService.getWashStatus(anyLong())).thenReturn(WashStatus.RUNNING);

        job.execute(mockContext);

        InOrder order = inOrder(mockWashService, mockApplianceService);
        order.verify(mockWashService).getWashStatus(1L);
        order.verify(mockWashService).setWashFinishTime(eq(1L), any(LocalDateTime.class));
        order.verify(mockWashService).setWashStatus(1L, WashStatus.FINISHED);
        order.verify(mockApplianceService).setApplianceStatus(2L, ApplianceStatus.IDLE);
        verifyNoMoreInteractions(mockWashService, mockApplianceService);
    }

    @Test
    public void testExecute_needToFinish_washRunning_cancelingWash() throws NotFoundException, JobExecutionException {
        job.setCancelingWash(true);
        when(mockWashService.getWashStatus(anyLong())).thenReturn(WashStatus.RUNNING);

        job.execute(mockContext);

        InOrder order = inOrder(mockWashService, mockApplianceService);
        order.verify(mockWashService).getWashStatus(1L);
        order.verify(mockWashService).setWashFinishTime(eq(1L), any(LocalDateTime.class));
        order.verify(mockApplianceService).setApplianceStatus(2L, ApplianceStatus.IDLE);
        order.verify(mockWashService).setWashStatus(1L, WashStatus.CANCELED);
        verifyNoMoreInteractions(mockWashService, mockApplianceService);
    }

    @Test
    public void testExecute_needToFinish_washWaiting_cancelingWash() throws NotFoundException, JobExecutionException {
        job.setCancelingWash(true);
        when(mockWashService.getWashStatus(anyLong())).thenReturn(WashStatus.WAITING);

        job.execute(mockContext);

        InOrder order = inOrder(mockWashService, mockApplianceService);
        order.verify(mockWashService).getWashStatus(1L);
        order.verify(mockWashService).setWashStatus(1L, WashStatus.CANCELED);
        verifyNoMoreInteractions(mockWashService);
        verifyNoInteractions(mockApplianceService);
    }

}
