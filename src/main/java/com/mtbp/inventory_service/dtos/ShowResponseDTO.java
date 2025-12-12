package com.mtbp.inventory_service.dtos;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record ShowResponseDTO (
    String theatreId,
    String theatreName,
    Map<String, ShowDTO> showDetails
    ){ }
