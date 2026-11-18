package com.freightfox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LaneDto {
    @NotNull(message = "Lane id is required")
    private Long id;

    @NotBlank(message = "Lane origin is required")
    private String origin;

    @NotBlank(message = "Lane destination is required")
    private String destination;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}
