package com.freightfox.algorithm;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExhaustiveAssignmentStrategy implements AssignmentStrategy {

    @Override
    public Result solve(Set<Long> laneIds,
                        List<Long> transporterIds,
                        Map<Long, Map<Long, Integer>> quotes,
                        int maxTransporters) {

        // Step 1: Feasibility check — every lane must have at least one quote
        for (Long laneId : laneIds) {
            boolean covered = transporterIds.stream()
                    .anyMatch(tid -> quotes.getOrDefault(tid, Collections.emptyMap()).containsKey(laneId));
            if (!covered)
                throw new IllegalStateException("Lane " + laneId + " has no quotes — assignment is infeasible.");
        }

        // Step 2: Detect forced transporters — lanes with only one quoter
        Set<Long> forcedSet = new HashSet<>();
        for (Long laneId : laneIds) {
            List<Long> quoters = transporterIds.stream()
                    .filter(tid -> quotes.getOrDefault(tid, Collections.emptyMap()).containsKey(laneId))
                    .collect(Collectors.toList());
            if (quoters.size() == 1)
                forcedSet.add(quoters.get(0));
        }

        // Step 3: If forced set already exceeds maxTransporters, infeasible
        if (forcedSet.size() > maxTransporters)
            throw new IllegalStateException("Infeasible: " + forcedSet.size()
                    + " transporters are forced but maxTransporters is " + maxTransporters + ".");

        // Step 4: Free transporters sorted once for deterministic tie-breaking
        List<Long> forced = new ArrayList<>(forcedSet);
        List<Long> candidates = transporterIds.stream()
                .filter(tid -> !forcedSet.contains(tid))
                .sorted()
                .collect(Collectors.toList());

        int remainingSlots = maxTransporters - forced.size();

        int[] bestCost = {Integer.MAX_VALUE};
        Map<Long, Long>[] bestAssignment = new Map[]{null};
        Set<Long>[] bestSelected = new Set[]{null};

        // Step 5: Branch and bound
        branchAndBound(forced, candidates, 0, remainingSlots,
                laneIds, quotes, bestCost, bestAssignment, bestSelected);

        if (bestAssignment[0] == null)
            throw new IllegalStateException("No feasible assignment exists for maxTransporters=" + maxTransporters + ".");

        return new Result(bestAssignment[0], bestSelected[0], bestCost[0]);
    }

    private void branchAndBound(List<Long> chosen,
                                List<Long> candidates,
                                int start,
                                int slotsLeft,
                                Set<Long> laneIds,
                                Map<Long, Map<Long, Integer>> quotes,
                                int[] bestCost,
                                Map<Long, Long>[] bestAssignment,
                                Set<Long>[] bestSelected) {

        int[] eval = evaluate(chosen, laneIds, quotes);
        boolean allCovered = eval[1] == 1;

        if (allCovered) {
            int cost = eval[0];
            boolean better = cost < bestCost[0]
                    || (cost == bestCost[0] && bestSelected[0] != null
                        && chosen.size() > bestSelected[0].size()); // tie-break: more transporters
            if (better) {
                bestCost[0] = cost;
                bestAssignment[0] = buildAssignment(chosen, laneIds, quotes);
                bestSelected[0] = new HashSet<>(chosen);
            }
            // NO return — supersets may be cheaper
        }

        if (slotsLeft == 0) return;

        // Prune with real lower bound over chosen + remaining candidates
        long lb = lowerBound(chosen, candidates, start, laneIds, quotes);
        if (lb < 0 || lb >= bestCost[0]) return; // infeasible branch or can't beat best

        for (int i = start; i < candidates.size(); i++) {
            chosen.add(candidates.get(i));
            branchAndBound(chosen, candidates, i + 1, slotsLeft - 1,
                    laneIds, quotes, bestCost, bestAssignment, bestSelected);
            chosen.remove(chosen.size() - 1);
        }
    }

    /**
     * Lower bound: cheapest quote per lane over chosen + candidates[start..].
     * Returns -1 if any lane cannot be covered by this pool.
     */
    private long lowerBound(List<Long> chosen, List<Long> candidates, int start,
                            Set<Long> laneIds, Map<Long, Map<Long, Integer>> quotes) {
        long total = 0;
        for (Long laneId : laneIds) {
            int cheapest = Integer.MAX_VALUE;
            for (Long tid : chosen) {
                Integer q = quotes.getOrDefault(tid, Collections.emptyMap()).get(laneId);
                if (q != null && q < cheapest) cheapest = q;
            }
            for (int i = start; i < candidates.size(); i++) {
                Integer q = quotes.getOrDefault(candidates.get(i), Collections.emptyMap()).get(laneId);
                if (q != null && q < cheapest) cheapest = q;
            }
            if (cheapest == Integer.MAX_VALUE) return -1;
            total += cheapest;
        }
        return total;
    }

    private int[] evaluate(List<Long> chosen, Set<Long> laneIds,
                           Map<Long, Map<Long, Integer>> quotes) {
        int cost = 0;
        boolean allCovered = true;
        for (Long laneId : laneIds) {
            int cheapest = Integer.MAX_VALUE;
            for (Long tid : chosen) {
                Integer q = quotes.getOrDefault(tid, Collections.emptyMap()).get(laneId);
                if (q != null && q < cheapest) cheapest = q;
            }
            if (cheapest == Integer.MAX_VALUE) allCovered = false;
            else cost += cheapest;
        }
        return new int[]{cost, allCovered ? 1 : 0};
    }

    private Map<Long, Long> buildAssignment(List<Long> chosen, Set<Long> laneIds,
                                            Map<Long, Map<Long, Integer>> quotes) {
        List<Long> sortedChosen = new ArrayList<>(chosen);
        Collections.sort(sortedChosen);
        Map<Long, Long> assignment = new HashMap<>();
        for (Long laneId : laneIds) {
            int cheapest = Integer.MAX_VALUE;
            Long chosenTid = null;
            for (Long tid : sortedChosen) {
                Integer q = quotes.getOrDefault(tid, Collections.emptyMap()).get(laneId);
                if (q != null && q < cheapest) {
                    cheapest = q;
                    chosenTid = tid;
                }
            }
            assignment.put(laneId, chosenTid);
        }
        return assignment;
    }
}
