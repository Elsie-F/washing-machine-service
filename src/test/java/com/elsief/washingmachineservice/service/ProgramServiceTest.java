package com.elsief.washingmachineservice.service;

import com.elsief.washingmachineservice.entity.Program;
import com.elsief.washingmachineservice.repository.ProgramRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProgramServiceTest {

    @Mock
    ProgramRepo mockProgramRepo;

    @InjectMocks
    private ProgramService programService;

    private Program program;

    @Before
    public void setUp() {
        program = Program.builder().id(1).name("someName").durationInMinutes(120).temperature(60).spinSpeed(800).build();
        when(mockProgramRepo.findById(anyLong())).thenReturn(Optional.of(program));
    }

    @Test
    public void testFindById() {
        assertEquals(Optional.of(program), programService.findById(1));
        verify(mockProgramRepo).findById(1L);
        verifyNoMoreInteractions(mockProgramRepo);
    }

    @Test
    public void testFindById_notFound() {
        when(mockProgramRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), programService.findById(1));
        verify(mockProgramRepo).findById(1L);
        verifyNoMoreInteractions(mockProgramRepo);
    }
}
