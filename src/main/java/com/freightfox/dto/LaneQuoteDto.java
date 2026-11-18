package com.freightfox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LaneQuoteDto {

    @NotNull(message = "laneId is required")
    private Long laneId;

    @Positive(message = "Quote must be positive")
    private int quote;

    public Long getLaneId() { return laneId; }
    public void setLaneId(Long laneId) { this.laneId = laneId; }
    public int getQuote() { return quote; }
    public void setQuote(int quote) { this.quote = quote; }
}
