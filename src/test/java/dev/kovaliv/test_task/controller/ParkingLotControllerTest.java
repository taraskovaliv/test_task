package dev.kovaliv.test_task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kovaliv.test_task.data.dto.CreateParkingLotDTO;
import dev.kovaliv.test_task.data.dto.ParkingLotDTO;
import dev.kovaliv.test_task.service.ParkingLotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingLotController.class)
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingLotService parkingLotService;

    @Test
    void shouldCreateParkingLotSuccessfully() throws Exception {
        // Given
        CreateParkingLotDTO requestDto = CreateParkingLotDTO.builder()
                .name("Test Parking Lot")
                .build();

        ParkingLotDTO responseDto = ParkingLotDTO.builder()
                .id(1L)
                .name("Test Parking Lot")
                .build();

        when(parkingLotService.createParkingLot(any(CreateParkingLotDTO.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Parking Lot"));

        verify(parkingLotService).createParkingLot(any(CreateParkingLotDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        // Given
        when(parkingLotService.createParkingLot(any(CreateParkingLotDTO.class)))
                .thenThrow(new IllegalArgumentException("Parking lot name cannot be null or empty."));

        CreateParkingLotDTO requestDto = CreateParkingLotDTO.builder()
                .name("")
                .build();

        // When & Then
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingLotService).createParkingLot(any(CreateParkingLotDTO.class));
    }

    @Test
    void shouldReturnBadRequestForDuplicateName() throws Exception {
        // Given
        when(parkingLotService.createParkingLot(any(CreateParkingLotDTO.class)))
                .thenThrow(new IllegalArgumentException("Parking lot with this name already exists."));

        CreateParkingLotDTO requestDto = CreateParkingLotDTO.builder()
                .name("Existing Lot")
                .build();

        // When & Then
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingLotService).createParkingLot(any(CreateParkingLotDTO.class));
    }

    @Test
    void shouldDeleteParkingLotSuccessfully() throws Exception {
        // Given
        Long parkingLotId = 1L;
        doNothing().when(parkingLotService).deleteParkingLot(parkingLotId);

        // When & Then
        mockMvc.perform(delete("/parking-lots/{id}", parkingLotId))
                .andExpect(status().isNoContent());

        verify(parkingLotService).deleteParkingLot(eq(parkingLotId));
    }

    @Test
    void shouldReturnBadRequestWhenDeletingNonExistentLot() throws Exception {
        // Given
        Long parkingLotId = 999L;
        doThrow(new IllegalArgumentException("Parking lot with id " + parkingLotId + " does not exist."))
                .when(parkingLotService).deleteParkingLot(parkingLotId);

        // When & Then
        mockMvc.perform(delete("/parking-lots/{id}", parkingLotId))
                .andExpect(status().isBadRequest());

        verify(parkingLotService).deleteParkingLot(eq(parkingLotId));
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(parkingLotService, never()).createParkingLot(any());
    }

    @Test
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(parkingLotService, never()).createParkingLot(any());
    }

    @Test
    void shouldHandleInvalidPathVariable() throws Exception {
        // When & Then
        mockMvc.perform(delete("/parking-lots/invalid"))
                .andExpect(status().isBadRequest());

        verify(parkingLotService, never()).deleteParkingLot(any());
    }
}