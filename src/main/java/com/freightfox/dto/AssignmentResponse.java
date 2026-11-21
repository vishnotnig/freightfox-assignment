package com.freightfox.dto;

import java.util.List;

public record AssignmentResponse(
        String status,
        long totalCost,
        List<LaneAssignment> assignments,
        List<Long> selectedTransporters
) {}
