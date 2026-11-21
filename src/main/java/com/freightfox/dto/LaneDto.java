package com.freightfox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LaneDto(
        @NotNull(message = "Lane id is required") Long id,
        @NotBlank(message = "Lane origin is required") String origin,
        @NotBlank(message = "Lane destination is required") String destination
) {}
