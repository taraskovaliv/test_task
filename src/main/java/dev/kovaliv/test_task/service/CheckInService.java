package dev.kovaliv.test_task.service;

import dev.kovaliv.test_task.data.dto.CheckInDto;
import dev.kovaliv.test_task.data.dto.CheckInResponseDTO;
import dev.kovaliv.test_task.data.dto.CheckOutDto;
import dev.kovaliv.test_task.data.dto.CheckOutResponseDTO;

import java.util.List;

public interface CheckInService {

    CheckInResponseDTO checkIn(Long lotId, CheckInDto checkInDto);

    CheckOutResponseDTO checkOut(Long lotId, CheckOutDto checkOutDto);

    List<CheckInResponseDTO> getActiveCheckIns(Long lotId);
}
