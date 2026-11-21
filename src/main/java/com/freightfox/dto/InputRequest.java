package com.freightfox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InputRequest(
        @NotEmpty(message = "Lanes list must not be empty") @Valid List<LaneDto> lanes,
        @NotEmpty(message = "Transporters list must not be empty") @Valid List<TransporterDto> transporters
) {}
