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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class StartWashJobTest {
    @Mock
    private ApplianceService mockApplianceService;
    @Mock
    private WashService mockWashService;
    @Mock
    private JobExecutionContext mockContext;

    private StartWashJob job;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        job = new StartWashJob();
        job.setWashId(1L);
        job.setApplianceId(2L);
        job.setApplianceService(mockApplianceService);
        job.setWashService(mockWashService);
    }

    @DataProvider
    public static Object[][] provideWashStatus() {
        return new Object[][] {
                {WashStatus.RUNNING},
                {WashStatus.FINISHED},
                {WashStatus.CANCELED}
        };
    }

    @Test
    @UseDataProvider("provideWashStatus")
    public void testExecute_washRunningOrFinishedOrCanceled(WashStatus washStatus) throws NotFoundException, JobExecutionException {
        when(mockWashService.getWashStatus(anyLong())).thenReturn(washStatus);

        job.execute(mockContext);

        verify(mockWashService).getWashStatus(1L);
        verifyNoMoreInteractions(mockWashService);
        verifyNoInteractions(mockApplianceService);
    }

    @Test
    public void testExecute_washWaiting_applianceRunning() throws NotFoundException, JobExecutionException {
        when(mockWashService.getWashStatus(anyLong())).thenReturn(WashStatus.WAITING);
        when(mockApplianceService.getApplianceStatus(anyLong())).thenReturn(ApplianceStatus.RUNNING);

        job.execute(mockContext);

        InOrder order = inOrder(mockWashService, mockApplianceService);
        order.verify(mockWashService).getWashStatus(1L);
        order.verify(mockApplianceService).getApplianceStatus(2L);
        order.verify(mockWashService).setWashStatus(1L, WashStatus.CANCELED);
        verifyNoMoreInteractions(mockWashService, mockWashService);
    }

    @Test
    public void testExecute_washWaiting_applianceIdle() throws NotFoundException, JobExecutionException {
        when(mockWashService.getWashStatus(anyLong())).thenReturn(WashStatus.WAITING);
        when(mockApplianceService.getApplianceStatus(anyLong())).thenReturn(ApplianceStatus.IDLE);

        job.execute(mockContext);

        InOrder order = inOrder(mockWashService, mockApplianceService);
        order.verify(mockWashService).getWashStatus(1L);
        order.verify(mockApplianceService).getApplianceStatus(2L);
        order.verify(mockApplianceService).setApplianceStatus(2L, ApplianceStatus.RUNNING);
        order.verify(mockWashService).setWashStatus(1L, WashStatus.RUNNING);
        verifyNoMoreInteractions(mockWashService, mockWashService);
    }
}
