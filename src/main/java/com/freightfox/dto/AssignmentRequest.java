package com.freightfox.dto;

import jakarta.validation.constraints.Min;

public class AssignmentRequest {

    @Min(value = 1, message = "maxTransporters must be at least 1")
    private int maxTransporters;

    public int getMaxTransporters() { return maxTransporters; }
    public void setMaxTransporters(int maxTransporters) { this.maxTransporters = maxTransporters; }
}
