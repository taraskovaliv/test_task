package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.AddParkingLevelDTO;
import dev.kovaliv.test_task.data.dto.ParkingLevelDTO;
import dev.kovaliv.test_task.service.ParkingLevelService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(ParkingLevelController.class)
class ParkingLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingLevelService parkingLevelService;

    @Test
    void shouldCreateParkingLevelSuccessfully() throws Exception {
        // Given
        AddParkingLevelDTO requestDto = AddParkingLevelDTO.builder()
                .parkingLotId(1L)
                .floor(2)
                .build();

        ParkingLevelDTO responseDto = ParkingLevelDTO.builder()
                .id(1L)
                .floor(2)
                .parkingLotId(1L)
                .build();

        when(parkingLevelService.createParkingLevel(any(AddParkingLevelDTO.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/parking-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.floor").value(2))
                .andExpect(jsonPath("$.parkingLotId").value(1));

        verify(parkingLevelService).createParkingLevel(any(AddParkingLevelDTO.class));
    }

    @Test
    void shouldReturnErrorForInvalidParkingLotId() throws Exception {
        // Given
        when(parkingLevelService.createParkingLevel(any(AddParkingLevelDTO.class)))
                .thenThrow(new IllegalArgumentException("Parking lot with id 999 does not exist."));

        AddParkingLevelDTO requestDto = AddParkingLevelDTO.builder()
                .parkingLotId(999L)
                .floor(1)
                .build();

        // When & Then
        mockMvc.perform(post("/parking-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService).createParkingLevel(any(AddParkingLevelDTO.class));
    }

    @Test
    void shouldReturnErrorForDuplicateFloor() throws Exception {
        // Given
        when(parkingLevelService.createParkingLevel(any(AddParkingLevelDTO.class)))
                .thenThrow(new IllegalArgumentException("Floor already exists in this parking lot."));

        AddParkingLevelDTO requestDto = AddParkingLevelDTO.builder()
                .parkingLotId(1L)
                .floor(1)
                .build();

        // When & Then
        mockMvc.perform(post("/parking-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService).createParkingLevel(any(AddParkingLevelDTO.class));
    }

    @Test
    void shouldDeleteParkingLevelSuccessfully() throws Exception {
        // Given
        Long levelId = 1L;
        doNothing().when(parkingLevelService).deleteParkingLevel(levelId);

        // When & Then
        mockMvc.perform(post("/parking-levels/{id}", levelId))
                .andExpect(status().isNoContent());

        verify(parkingLevelService).deleteParkingLevel(eq(levelId));
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistentLevel() throws Exception {
        // Given
        Long levelId = 999L;
        doThrow(new IllegalArgumentException("Parking level with id " + levelId + " does not exist."))
                .when(parkingLevelService).deleteParkingLevel(levelId);

        // When & Then
        mockMvc.perform(post("/parking-levels/{id}", levelId))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService).deleteParkingLevel(eq(levelId));
    }

    @Test
    void shouldHandleMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/parking-levels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService, never()).createParkingLevel(any());
    }

    @Test
    void shouldHandleInvalidPathVariable() throws Exception {
        // When & Then
        mockMvc.perform(post("/parking-levels/invalid"))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService, never()).deleteParkingLevel(any());
    }

    @Test
    void shouldHandleMissingRequestBody() throws Exception {
        mockMvc.perform(post("/parking-levels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(parkingLevelService, never()).createParkingLevel(any());
    }
}