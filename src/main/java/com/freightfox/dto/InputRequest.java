package com.freightfox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InputRequest {
    @NotEmpty(message = "Lanes list must not be empty")
    @Valid
    private List<LaneDto> lanes;

    @NotEmpty(message = "Transporters list must not be empty")
    @Valid
    private List<TransporterDto> transporters;

    public List<LaneDto> getLanes() { return lanes; }
    public void setLanes(List<LaneDto> lanes) { this.lanes = lanes; }
    public List<TransporterDto> getTransporters() { return transporters; }
    public void setTransporters(List<TransporterDto> transporters) { this.transporters = transporters; }
}
