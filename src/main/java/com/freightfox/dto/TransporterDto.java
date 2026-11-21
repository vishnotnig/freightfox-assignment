package com.freightfox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TransporterDto(
        @NotNull(message = "Transporter id is required") Long id,
        @NotBlank(message = "Transporter name is required") String name,
        @NotEmpty(message = "Transporter must have at least one lane quote") @Valid List<LaneQuoteDto> laneQuotes
) {}
