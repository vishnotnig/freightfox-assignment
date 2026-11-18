package com.freightfox.service;

import com.freightfox.algorithm.AssignmentStrategy;
import com.freightfox.dto.*;
import com.freightfox.model.CostMatrix;
import com.freightfox.model.Lane;
import com.freightfox.model.Transporter;
import com.freightfox.repository.CostMatrixRepository;
import com.freightfox.repository.LaneRepository;
import com.freightfox.repository.TransporterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final LaneRepository laneRepository;
    private final TransporterRepository transporterRepository;
    private final CostMatrixRepository costMatrixRepository;
    private final AssignmentStrategy assignmentStrategy;

    public AssignmentService(LaneRepository laneRepository,
                             TransporterRepository transporterRepository,
                             CostMatrixRepository costMatrixRepository,
                             AssignmentStrategy assignmentStrategy) {
        this.laneRepository = laneRepository;
        this.transporterRepository = transporterRepository;
        this.costMatrixRepository = costMatrixRepository;
        this.assignmentStrategy = assignmentStrategy;
    }

    @Transactional
    public void saveInput(InputRequest request) {
        costMatrixRepository.deleteAll();
        laneRepository.deleteAll();
        transporterRepository.deleteAll();

        for (LaneDto ld : request.getLanes()) {
            Lane lane = new Lane();
            lane.setId(ld.getId());
            lane.setOrigin(ld.getOrigin());
            lane.setDestination(ld.getDestination());
            laneRepository.save(lane);
        }

        for (TransporterDto td : request.getTransporters()) {
            Transporter transporter = new Transporter();
            transporter.setId(td.getId());
            transporter.setName(td.getName());
            transporterRepository.save(transporter);

            for (LaneQuoteDto lq : td.getLaneQuotes()) {
                Lane lane = laneRepository.findById(lq.getLaneId())
                        .orElseThrow(() -> new RuntimeException("Lane not found: " + lq.getLaneId()));
                CostMatrix cm = new CostMatrix();
                cm.setLane(lane);
                cm.setTransporter(transporter);
                cm.setCost(lq.getQuote());
                costMatrixRepository.save(cm);
            }
        }
    }

    public AssignmentResponse optimize(int maxTransporters) {
        List<Lane> lanes = laneRepository.findAll();
        if (lanes.isEmpty())
            throw new IllegalStateException("No input data found. Call POST /api/v1/transporters/input first.");
        List<Transporter> transporters = transporterRepository.findAll();
        List<CostMatrix> allQuotes = costMatrixRepository.findAll();

        Set<Long> laneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
        List<Long> transporterIds = transporters.stream().map(Transporter::getId).collect(Collectors.toList());

        // Build quote map: transporterId -> { laneId -> cost }
        Map<Long, Map<Long, Integer>> quotes = new HashMap<>();
        for (CostMatrix cm : allQuotes) {
            quotes.computeIfAbsent(cm.getTransporter().getId(), k -> new HashMap<>())
                  .put(cm.getLane().getId(), cm.getCost());
        }

        AssignmentStrategy.Result result = assignmentStrategy.solve(laneIds, transporterIds, quotes, maxTransporters);

        if (result == null)
            return new AssignmentResponse("error", 0, Collections.emptyList(), Collections.emptyList());

        List<AssignmentResult> assignments = lanes.stream()
                .sorted(Comparator.comparing(Lane::getId))
                .map(lane -> new AssignmentResult(lane.getId(), result.assignment.get(lane.getId())))
                .collect(Collectors.toList());

        List<Long> selectedList = new ArrayList<>(result.selectedTransporters);
        Collections.sort(selectedList);

        return new AssignmentResponse("success", result.totalCost, assignments, selectedList);
    }
}
