package com.freightfox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class TransporterDto {
    @NotNull(message = "Transporter id is required")
    private Long id;

    @NotBlank(message = "Transporter name is required")
    private String name;

    @NotEmpty(message = "Transporter must have at least one lane quote")
    @Valid
    private List<LaneQuoteDto> laneQuotes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<LaneQuoteDto> getLaneQuotes() { return laneQuotes; }
    public void setLaneQuotes(List<LaneQuoteDto> laneQuotes) { this.laneQuotes = laneQuotes; }
}
