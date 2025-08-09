package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.AddParkingSlotDTO;
import dev.kovaliv.test_task.data.dto.ParkingSlotDTO;
import dev.kovaliv.test_task.data.entity.SlotType;
import dev.kovaliv.test_task.service.ParkingSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingSlotController.class)
class ParkingSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ParkingSlotService parkingSlotService;

    @Test
    void shouldAddSlotsToLevelSuccessfully() throws Exception {
        AddParkingSlotDTO requestDto = AddParkingSlotDTO.builder()
                .parkingLevelId(1L)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(3)
                .build();

        List<ParkingSlotDTO> responseDto = Arrays.asList(
                ParkingSlotDTO.builder()
                        .id(1L)
                        .slotType(SlotType.COMPACT)
                        .occupied(false)
                        .parkingLevelId(1L)
                        .build(),
                ParkingSlotDTO.builder()
                        .id(2L)
                        .slotType(SlotType.COMPACT)
                        .occupied(false)
                        .parkingLevelId(1L)
                        .build(),
                ParkingSlotDTO.builder()
                        .id(3L)
                        .slotType(SlotType.COMPACT)
                        .occupied(false)
                        .parkingLevelId(1L)
                        .build()
        );

        when(parkingSlotService.addSlotsToLevel(any(AddParkingSlotDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].slotType").value("COMPACT"))
                .andExpect(jsonPath("$[0].occupied").value(false))
                .andExpect(jsonPath("$[0].parkingLevelId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(3));

        verify(parkingSlotService).addSlotsToLevel(any(AddParkingSlotDTO.class));
    }

    @Test
    void shouldReturnErrorForNullParkingLevelId() throws Exception {
        when(parkingSlotService.addSlotsToLevel(any(AddParkingSlotDTO.class)))
                .thenThrow(new IllegalArgumentException("Parking level ID cannot be null."));

        AddParkingSlotDTO requestDto = AddParkingSlotDTO.builder()
                .parkingLevelId(null)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(3)
                .build();

        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).addSlotsToLevel(any(AddParkingSlotDTO.class));
    }

    @Test
    void shouldReturnErrorForInvalidNumberOfSlots() throws Exception {
        // Given
        when(parkingSlotService.addSlotsToLevel(any(AddParkingSlotDTO.class)))
                .thenThrow(new IllegalArgumentException("Number of slots must be greater than 0."));

        AddParkingSlotDTO requestDto = AddParkingSlotDTO.builder()
                .parkingLevelId(1L)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(0)
                .build();

        // When & Then
        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).addSlotsToLevel(any(AddParkingSlotDTO.class));
    }

    @Test
    void shouldReturnErrorForNonExistentParkingLevel() throws Exception {
        // Given
        when(parkingSlotService.addSlotsToLevel(any(AddParkingSlotDTO.class)))
                .thenThrow(new IllegalArgumentException("Parking level with id 999 does not exist."));

        AddParkingSlotDTO requestDto = AddParkingSlotDTO.builder()
                .parkingLevelId(999L)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(3)
                .build();

        // When & Then
        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).addSlotsToLevel(any(AddParkingSlotDTO.class));
    }

    @Test
    void shouldRemoveSlotSuccessfully() throws Exception {
        // Given
        Long slotId = 1L;
        doNothing().when(parkingSlotService).removeSlot(slotId);

        // When & Then
        mockMvc.perform(delete("/parking-slots/{slotId}", slotId))
                .andExpect(status().isNoContent());

        verify(parkingSlotService).removeSlot(eq(slotId));
    }

    @Test
    void shouldReturnErrorWhenRemovingNonExistentSlot() throws Exception {
        // Given
        Long slotId = 999L;
        doThrow(new IllegalArgumentException("Parking slot with id " + slotId + " does not exist."))
                .when(parkingSlotService).removeSlot(slotId);

        // When & Then
        mockMvc.perform(delete("/parking-slots/{slotId}", slotId))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).removeSlot(eq(slotId));
    }

    @Test
    void shouldReturnErrorWhenRemovingOccupiedSlot() throws Exception {
        // Given
        Long slotId = 1L;
        doThrow(new IllegalArgumentException("Cannot remove occupied parking slot."))
                .when(parkingSlotService).removeSlot(slotId);

        // When & Then
        mockMvc.perform(delete("/parking-slots/{slotId}", slotId))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).removeSlot(eq(slotId));
    }

    @Test
    void shouldHandleInvalidSlotId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/parking-slots/invalid"))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService, never()).removeSlot(any());
    }

    @Test
    void shouldHandleMalformedJsonForAddSlots() throws Exception {
        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService, never()).addSlotsToLevel(any());
    }

    @Test
    void shouldHandleMissingRequestBodyForAddSlots() throws Exception {
        // When & Then
        mockMvc.perform(post("/parking-slots")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService, never()).addSlotsToLevel(any());
    }

    @Test
    void shouldHandleAllSlotTypes() throws Exception {
        // Test that all slot types can be processed
        for (SlotType slotType : SlotType.values()) {
            AddParkingSlotDTO requestDto = AddParkingSlotDTO.builder()
                    .parkingLevelId(1L)
                    .slotType(slotType)
                    .numberOfSlots(1)
                    .build();

            List<ParkingSlotDTO> responseDto = Collections.singletonList(
                    ParkingSlotDTO.builder()
                            .id(1L)
                            .slotType(slotType)
                            .occupied(false)
                            .parkingLevelId(1L)
                            .build()
            );

            when(parkingSlotService.addSlotsToLevel(any(AddParkingSlotDTO.class)))
                    .thenReturn(responseDto);

            mockMvc.perform(post("/parking-slots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].slotType").value(slotType.name()));

            reset(parkingSlotService);
        }
    }

    @Test
    void shouldUpdateSlotAvailabilitySuccessfully() throws Exception {
        Long slotId = 1L;
        boolean occupied = true;

        doNothing().when(parkingSlotService).updateSlotAvailability(slotId, occupied);

        mockMvc.perform(post("/parking-slots/{slotId}/availability", slotId)
                        .param("occupied", String.valueOf(occupied)))
                .andExpect(status().isNoContent());

        verify(parkingSlotService).updateSlotAvailability(eq(slotId), eq(occupied));
    }

    @Test
    void shouldReturnErrorForInvalidSlotIdOnUpdate() throws Exception {
        Long slotId = 999L;
        boolean occupied = true;

        doThrow(new IllegalArgumentException("Parking slot with id " + slotId + " does not exist."))
                .when(parkingSlotService).updateSlotAvailability(slotId, occupied);

        mockMvc.perform(post("/parking-slots/{slotId}/availability", slotId)
                        .param("occupied", String.valueOf(occupied)))
                .andExpect(status().isBadRequest());

        verify(parkingSlotService).updateSlotAvailability(eq(slotId), eq(occupied));
    }
}