package com.freightfox.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ExhaustiveAssignmentStrategyTest {

    private ExhaustiveAssignmentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ExhaustiveAssignmentStrategy();
    }

    private Map<Long, Map<Long, Integer>> testCase1Quotes() {
        int[][] costs = {
            {20835, 10512, 22105, 42481, 19862},
            {48844, 31326, 18640, 45828, 18297},
            {39020, 20648, 31438, 36447, 12789},
            {14400, 44514, 14316, 10678, 13032},
            {11601, 19760, 40870, 20635, 26421},
            {35095, 12494, 17808, 36210, 39444},
            {26070, 41098, 20932, 16897, 27938}
        };
        Map<Long, Map<Long, Integer>> quotes = new HashMap<>();
        for (int t = 0; t < 7; t++) {
            Map<Long, Integer> laneQuotes = new HashMap<>();
            for (int l = 0; l < 5; l++)
                laneQuotes.put((long)(l + 1), costs[t][l]);
            quotes.put((long)(t + 1), laneQuotes);
        }
        return quotes;
    }

    private Map<Long, Map<Long, Integer>> testCase2Quotes() {
        Map<Long, Map<Long, Integer>> quotes = new HashMap<>();
        quotes.put(1L, Map.of(1L, 1000, 2L, 1500));
        quotes.put(2L, Map.of(3L, 2000, 4L, 1800));
        quotes.put(3L, Map.of(5L, 1200));
        quotes.put(4L, Map.of(1L, 1100, 4L, 1750));
        return quotes;
    }

    @Test
    void testCase1_max3() {
        Set<Long> lanes = Set.of(1L, 2L, 3L, 4L, 5L);
        List<Long> transporters = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);
        AssignmentStrategy.Result result = strategy.solve(lanes, transporters, testCase1Quotes(), 3);
        assertEquals(60139L, result.totalCost);
        assertEquals(Set.of(1L, 4L, 5L), result.selectedTransporters);
    }

    @Test
    void testCase1_max1_feasible() {
        Set<Long> lanes = Set.of(1L, 2L, 3L, 4L, 5L);
        List<Long> transporters = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);
        AssignmentStrategy.Result result = strategy.solve(lanes, transporters, testCase1Quotes(), 1);
        assertNotNull(result);
        assertEquals(1, result.selectedTransporters.size());
    }

    @Test
    void testCase2_max3() {
        Set<Long> lanes = Set.of(1L, 2L, 3L, 4L, 5L);
        List<Long> transporters = List.of(1L, 2L, 3L, 4L);
        AssignmentStrategy.Result result = strategy.solve(lanes, transporters, testCase2Quotes(), 3);
        assertEquals(7500L, result.totalCost);
        assertEquals(Set.of(1L, 2L, 3L), result.selectedTransporters);
    }

    @Test
    void testCase2_max4() {
        Set<Long> lanes = Set.of(1L, 2L, 3L, 4L, 5L);
        List<Long> transporters = List.of(1L, 2L, 3L, 4L);
        AssignmentStrategy.Result result = strategy.solve(lanes, transporters, testCase2Quotes(), 4);
        assertEquals(7450L, result.totalCost);
        assertEquals(Set.of(1L, 2L, 3L, 4L), result.selectedTransporters);
    }

    @Test
    void testCase2_max2_infeasible() {
        Set<Long> lanes = Set.of(1L, 2L, 3L, 4L, 5L);
        List<Long> transporters = List.of(1L, 2L, 3L, 4L);
        assertThrows(IllegalStateException.class,
                () -> strategy.solve(lanes, transporters, testCase2Quotes(), 2));
    }

    @Test
    void testLaneWithNoQuotes_infeasible() {
        Set<Long> lanes = Set.of(1L, 2L);
        List<Long> transporters = List.of(1L);
        Map<Long, Map<Long, Integer>> quotes = Map.of(1L, Map.of(1L, 1000));
        assertThrows(IllegalStateException.class,
                () -> strategy.solve(lanes, transporters, quotes, 1));
    }
}
