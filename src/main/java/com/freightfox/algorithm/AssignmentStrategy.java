package com.freightfox.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssignmentStrategy {

    /**
     * Finds the optimal assignment of transporters to lanes.
     * Throws IllegalStateException if no feasible assignment exists.
     *
     * @param laneIds         all lane IDs that must be covered
     * @param transporterIds  all available transporter IDs
     * @param quotes          transporterId -> { laneId -> cost }
     * @param maxTransporters maximum number of distinct transporters to use
     * @return optimal assignment result
     */
    Result solve(Set<Long> laneIds,
                 List<Long> transporterIds,
                 Map<Long, Map<Long, Integer>> quotes,
                 int maxTransporters);

    class Result {
        public final Map<Long, Long> assignment; // laneId -> transporterId
        public final Set<Long> selectedTransporters;
        public final long totalCost;

        public Result(Map<Long, Long> assignment, Set<Long> selectedTransporters, long totalCost) {
            this.assignment = assignment;
            this.selectedTransporters = selectedTransporters;
            this.totalCost = totalCost;
        }
    }
}
