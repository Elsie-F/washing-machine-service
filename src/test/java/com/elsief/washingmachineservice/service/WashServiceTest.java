package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Appliance;
import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.entity.Wash;
import com.elsief.washingmachineservice.enums.WashStatus;
import com.elsief.washingmachineservice.repository.WashRepo;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WashServiceTest {

    @Mock
    WashRepo mockWashRepo;
    @Mock
    Appliance mockAppliance;
    @Mock
    Program mockProgram;

    @InjectMocks
    private WashService washService;

    private Wash wash;

    @Before
    public void setUp() {
        wash = Wash.builder()
                .id(1)
                .appliance(mockAppliance)
                .program(mockProgram)
                .startTime(LocalDateTime.parse("2021-04-21 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(WashStatus.WAITING)
                .build();
        when(mockWashRepo.findById(anyLong())).thenReturn(Optional.of(wash));
    }

    @Test
    public void testCreateWash() {
        when(mockWashRepo.save(any())).thenReturn(wash);
        Wash washToCreate = Wash.builder()
                .appliance(mockAppliance)
                .program(mockProgram)
                .startTime(LocalDateTime.parse("2021-04-21 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(WashStatus.WAITING)
                .build();
        assertEquals(wash, washService.create(washToCreate));
        verify(mockWashRepo).save(washToCreate);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test
    public void testFindById() {
        assertEquals(Optional.of(wash), washService.findById(1));
        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test
    public void testFindById_notFound() {
        when(mockWashRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), washService.findById(1));
        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test
    public void testSetWashStatus() throws NotFoundException {
        Wash expectedWash = Wash.builder().id(1).appliance(mockAppliance)
                .program(mockProgram)
                .startTime(LocalDateTime.parse("2021-04-21 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(WashStatus.RUNNING).build();
        when(mockWashRepo.save(any())).thenReturn(expectedWash);

        Wash actualWash = washService.setWashStatus(1L, WashStatus.RUNNING);
        assertEquals(expectedWash, actualWash);

        verify(mockWashRepo).findById(1L);
        verify(mockWashRepo).save(expectedWash);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test(expected = NotFoundException.class)
    public void testSetWashStatus_washNotFound() throws NotFoundException {
        when(mockWashRepo.findById(anyLong())).thenReturn(Optional.empty());

        washService.setWashStatus(1L, WashStatus.RUNNING);

        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test
    public void testSetFinishTime() throws NotFoundException {
        Wash expectedWash = Wash.builder().id(1).appliance(mockAppliance)
                .program(mockProgram)
                .startTime(LocalDateTime.parse("2021-04-21 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .finishTime(LocalDateTime.parse("2021-04-21 15:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .status(WashStatus.WAITING).build();
        when(mockWashRepo.save(any())).thenReturn(expectedWash);

        Wash actualWash = washService.setWashFinishTime(1L, LocalDateTime.parse("2021-04-21 15:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertEquals(expectedWash, actualWash);

        verify(mockWashRepo).findById(1L);
        verify(mockWashRepo).save(expectedWash);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test(expected = NotFoundException.class)
    public void testSetFinishTime_washNotFound() throws NotFoundException {
        when(mockWashRepo.findById(anyLong())).thenReturn(Optional.empty());

        washService.setWashFinishTime(1L, LocalDateTime.parse("2021-04-21 15:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test
    public void testGetApplianceStatus() throws NotFoundException {
        WashStatus actualStatus = washService.getWashStatus(1L);

        assertEquals(WashStatus.WAITING, actualStatus);

        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }

    @Test(expected = NotFoundException.class)
    public void testGetWashStatus_washNotFound() throws NotFoundException {
        when(mockWashRepo.findById(anyLong())).thenReturn(Optional.empty());

        washService.getWashStatus(1L);

        verify(mockWashRepo).findById(1L);
        verifyNoMoreInteractions(mockWashRepo);
    }
}
