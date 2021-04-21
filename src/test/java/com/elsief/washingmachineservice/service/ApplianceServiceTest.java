package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.enums.ApplianceStatus;
import com.elsief.washingmachineservice.repository.ApplianceRepo;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplianceServiceTest {

    @Mock
    ApplianceRepo mockApplianceRepo;

    @InjectMocks
    private ApplianceService applianceService;

    private Appliance appliance;

    @Before
    public void setUp() {
        appliance = Appliance.builder().id(1).name("someName").status(ApplianceStatus.IDLE).build();
        when(mockApplianceRepo.findById(anyLong())).thenReturn(Optional.of(appliance));
    }

    @Test
    public void testFindById() {
        assertEquals(Optional.of(appliance), applianceService.findById(1));
        verify(mockApplianceRepo).findById(1L);
        verifyNoMoreInteractions(mockApplianceRepo);
    }

    @Test
    public void testFindById_notFound() {
        when(mockApplianceRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), applianceService.findById(1));
        verify(mockApplianceRepo).findById(1L);
        verifyNoMoreInteractions(mockApplianceRepo);
    }

    @Test
    public void testSetApplianceStatus() throws NotFoundException {
        Appliance expectedAppliance =  Appliance.builder().id(1).name("someName").status(ApplianceStatus.RUNNING).build();
        when(mockApplianceRepo.save(any())).thenReturn(expectedAppliance);

        Appliance actualAppliance = applianceService.setApplianceStatus(1L, ApplianceStatus.RUNNING);
        assertEquals(expectedAppliance, actualAppliance);

        InOrder order = inOrder(mockApplianceRepo);
        order.verify(mockApplianceRepo).findById(1L);
        order.verify(mockApplianceRepo).save(expectedAppliance);
        verifyNoMoreInteractions(mockApplianceRepo);
    }

    @Test(expected = NotFoundException.class)
    public void testSetApplianceStatus_applianceNotFound() throws NotFoundException {
        when(mockApplianceRepo.findById(anyLong())).thenReturn(Optional.empty());

        applianceService.setApplianceStatus(1L, ApplianceStatus.RUNNING);

        verify(mockApplianceRepo).findById(1L);
        verifyNoMoreInteractions(mockApplianceRepo);
    }

    @Test
    public void testGetApplianceStatus() throws NotFoundException {
        ApplianceStatus actualStatus = applianceService.getApplianceStatus(1L);

        assertEquals(ApplianceStatus.IDLE, actualStatus);

        verify(mockApplianceRepo).findById(1L);
        verifyNoMoreInteractions(mockApplianceRepo);
    }

    @Test(expected = NotFoundException.class)
    public void testGetApplianceStatus_applianceNotFound() throws NotFoundException {
        when(mockApplianceRepo.findById(anyLong())).thenReturn(Optional.empty());

        applianceService.getApplianceStatus(1L);

        verify(mockApplianceRepo).findById(1L);
        verifyNoMoreInteractions(mockApplianceRepo);
    }
}
