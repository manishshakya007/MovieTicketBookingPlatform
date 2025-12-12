package com.mtbp.inventory_service.dtos;

import java.time.LocalTime;
import java.util.List;

public record ShowResponseDTO (
    String theatreId,
    String theatreName,
    List<LocalTime> showTimes
    ){ }
