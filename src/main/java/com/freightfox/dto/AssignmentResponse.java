package com.freightfox.dto;

import java.util.List;

public class AssignmentResponse {

    private String status;
    private int totalCost;
    private List<AssignmentResult> assignments;
    private List<Long> selectedTransporters;

    public AssignmentResponse() {}

    public AssignmentResponse(String status, int totalCost, List<AssignmentResult> assignments, List<Long> selectedTransporters) {
        this.status = status;
        this.totalCost = totalCost;
        this.assignments = assignments;
        this.selectedTransporters = selectedTransporters;
    }

    public String getStatus() { return status; }
    public int getTotalCost() { return totalCost; }
    public List<AssignmentResult> getAssignments() { return assignments; }
    public List<Long> getSelectedTransporters() { return selectedTransporters; }
}
