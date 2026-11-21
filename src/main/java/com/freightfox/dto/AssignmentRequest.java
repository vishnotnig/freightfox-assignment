package com.freightfox.dto;

import jakarta.validation.constraints.Min;

public record AssignmentRequest(
        @Min(value = 1, message = "maxTransporters must be at least 1") int maxTransporters
) {}
