package dev.kovaliv.test_task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kovaliv.test_task.data.dto.CheckInDto;
import dev.kovaliv.test_task.data.dto.CheckInResponseDTO;
import dev.kovaliv.test_task.data.dto.CheckOutDto;
import dev.kovaliv.test_task.data.dto.CheckOutResponseDTO;
import dev.kovaliv.test_task.service.CheckInService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CheckInController.class)
class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckInService checkInService;

    @Test
    void contextLoads() {
        // This test is just to ensure that the context loads successfully
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(checkInService);
    }

    @Test
    void shouldCheckInSuccessfully() throws Exception {
        CheckInResponseDTO responseDto = CheckInResponseDTO.builder()
                .vehicleType("Car")
                .licensePlate("ABC123")
                .entryTime(new java.sql.Timestamp(System.currentTimeMillis()))
                .slotId(1L)
                .levelId(1L)
                .levelFlour(1)
                .build();

        CheckInDto checkInDto = CheckInDto.builder()
                .vehicleType("Car")
                .licensePlate("ABC123")
                .build();

        when(checkInService.checkIn(anyLong(), any(CheckInDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/check-in/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleType").value("Car"))
                .andExpect(jsonPath("$.licensePlate").value("ABC123"))
                .andExpect(jsonPath("$.slotId").value(1))
                .andExpect(jsonPath("$.levelId").value(1))
                .andExpect(jsonPath("$.levelFlour").value(1));

        verify(checkInService).checkIn(anyLong(), any(CheckInDto.class));
    }

    @Test
    void shouldReturnErrorForInvalidCheckIn() throws Exception {
        CheckInDto checkInDto = CheckInDto.builder()
                .vehicleType("Car")
                .licensePlate("ABC123")
                .build();

        when(checkInService.checkIn(anyLong(), any(CheckInDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid check-in request"));

        mockMvc.perform(post("/check-in/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInDto)))
                .andExpect(status().isBadRequest());

        verify(checkInService).checkIn(anyLong(), any(CheckInDto.class));
    }

    @Test
    void shouldReturnActiveCheckIns() throws Exception {
        CheckInResponseDTO responseDto = CheckInResponseDTO.builder()
                .vehicleType("Car")
                .licensePlate("ABC123")
                .entryTime(new java.sql.Timestamp(System.currentTimeMillis()))
                .slotId(1L)
                .levelId(1L)
                .levelFlour(1)
                .build();

        when(checkInService.getActiveCheckIns(anyLong())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/active-check-ins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vehicleType").value("Car"))
                .andExpect(jsonPath("$[0].licensePlate").value("ABC123"))
                .andExpect(jsonPath("$[0].slotId").value(1))
                .andExpect(jsonPath("$[0].levelId").value(1))
                .andExpect(jsonPath("$[0].levelFlour").value(1));

        verify(checkInService).getActiveCheckIns(anyLong());
    }

    @Test
    void shouldReturnEmptyListForNoActiveCheckIns() throws Exception {
        when(checkInService.getActiveCheckIns(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/active-check-ins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(checkInService).getActiveCheckIns(anyLong());
    }

    @Test
    void shouldCheckOutSuccessfully() throws Exception {
        CheckOutResponseDTO responseDto = CheckOutResponseDTO.builder()
                .checkInDate(Timestamp.valueOf(LocalDateTime.of(2023, 10, 1, 10, 0)))
                .checkOutDate(Timestamp.valueOf(LocalDateTime.of(2023, 10, 1, 12, 5)))
                .fee(new BigDecimal("10.00"))
                .build();

        CheckOutDto checkOutDto = CheckOutDto.builder()
                .licensePlate("ABC123")
                .build();

        when(checkInService.checkOut(anyLong(), any(CheckOutDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/check-out/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkOutDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(10.00))
                .andExpect(jsonPath("$.duration").value("02:05:00"));

        verify(checkInService).checkOut(anyLong(), any(CheckOutDto.class));
    }

    @Test
    void shouldReturnErrorForInvalidCheckOut() throws Exception {
        CheckOutDto checkOutDto = CheckOutDto.builder()
                .licensePlate("ABC123")
                .build();

        when(checkInService.checkOut(anyLong(), any(CheckOutDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid check-out request"));

        mockMvc.perform(post("/check-out/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkOutDto)))
                .andExpect(status().isBadRequest());

        verify(checkInService).checkOut(anyLong(), any(CheckOutDto.class));
    }
}