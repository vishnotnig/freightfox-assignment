package com.freightfox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LaneQuoteDto(
        @NotNull(message = "laneId is required") Long laneId,
        @Positive(message = "Quote must be positive") int quote
) {}
