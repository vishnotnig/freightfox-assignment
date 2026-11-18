package com.freightfox.dto;

public class AssignmentResult {
    private Long laneId;
    private Long transporterId;

    public AssignmentResult(Long laneId, Long transporterId) {
        this.laneId = laneId;
        this.transporterId = transporterId;
    }

    public Long getLaneId() { return laneId; }
    public Long getTransporterId() { return transporterId; }
}
