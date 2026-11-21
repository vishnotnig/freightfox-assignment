package com.freightfox.service;

import com.freightfox.algorithm.AssignmentStrategy;
import com.freightfox.dto.*;
import com.freightfox.exception.BadRequestException;
import com.freightfox.model.TransporterQuote;
import com.freightfox.model.Lane;
import com.freightfox.model.Transporter;
import com.freightfox.repository.TransporterQuoteRepository;
import com.freightfox.repository.LaneRepository;
import com.freightfox.repository.TransporterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final LaneRepository laneRepository;
    private final TransporterRepository transporterRepository;
    private final TransporterQuoteRepository transporterQuoteRepository;
    private final AssignmentStrategy assignmentStrategy;

    public AssignmentServiceImpl(LaneRepository laneRepository,
                                 TransporterRepository transporterRepository,
                                 TransporterQuoteRepository transporterQuoteRepository,
                                 AssignmentStrategy assignmentStrategy) {
        this.laneRepository = laneRepository;
        this.transporterRepository = transporterRepository;
        this.transporterQuoteRepository = transporterQuoteRepository;
        this.assignmentStrategy = assignmentStrategy;
    }

    @Override
    @Transactional
    public void saveInput(InputRequest request) {
        // Duplicate checks — kept as loops because throwing inside stream lambdas requires ugly try-catch
        Set<Long> laneIds = new HashSet<>();
        for (LaneDto ld : request.lanes()) {
            if (!laneIds.add(ld.id()))
                throw new BadRequestException("Duplicate lane id: " + ld.id());
        }

        Set<Long> transporterIds = new HashSet<>();
        for (TransporterDto td : request.transporters()) {
            if (!transporterIds.add(td.id()))
                throw new BadRequestException("Duplicate transporter id: " + td.id());

            Set<Long> quotedLanes = new HashSet<>();
            for (LaneQuoteDto lq : td.laneQuotes()) {
                if (!quotedLanes.add(lq.laneId()))
                    throw new BadRequestException("Transporter " + td.id()
                            + " has duplicate quote for lane " + lq.laneId());
            }
        }

        transporterQuoteRepository.deleteAll();
        laneRepository.deleteAll();
        transporterRepository.deleteAll();

        Map<Long, Lane> savedLanes = request.lanes().stream()
                .map(ld -> {
                    Lane lane = new Lane();
                    lane.setId(ld.id());
                    lane.setOrigin(ld.origin());
                    lane.setDestination(ld.destination());
                    return laneRepository.save(lane);
                })
                .collect(Collectors.toMap(Lane::getId, l -> l));

        request.transporters().forEach(td -> {
            Transporter transporter = new Transporter();
            transporter.setId(td.id());
            transporter.setName(td.name());
            transporterRepository.save(transporter);

            td.laneQuotes().stream()
                    .map(lq -> {
                        Lane lane = Optional.ofNullable(savedLanes.get(lq.laneId()))
                                .orElseThrow(() -> new BadRequestException("Lane not found: " + lq.laneId()));
                        TransporterQuote tq = new TransporterQuote();
                        tq.setLane(lane);
                        tq.setTransporter(transporter);
                        tq.setCost(lq.quote());
                        return tq;
                    })
                    .forEach(transporterQuoteRepository::save);
        });
    }

    @Override
    public AssignmentResponse optimize(int maxTransporters) {
        List<Lane> lanes = laneRepository.findAll();
        if (lanes.isEmpty())
            throw new IllegalStateException("No input data found. Call POST /api/v1/transporters/input first.");

        List<Transporter> transporters = transporterRepository.findAll();
        List<TransporterQuote> allQuotes = transporterQuoteRepository.findAll();

        Set<Long> laneIds = lanes.stream().map(Lane::getId).collect(Collectors.toSet());
        List<Long> transporterIds = transporters.stream().map(Transporter::getId).collect(Collectors.toList());

        Map<Long, Map<Long, Integer>> quotes = allQuotes.stream()
                .collect(Collectors.groupingBy(
                        tq -> tq.getTransporter().getId(),
                        Collectors.toMap(tq -> tq.getLane().getId(), TransporterQuote::getCost)
                ));

        AssignmentStrategy.Result result = assignmentStrategy.solve(laneIds, transporterIds, quotes, maxTransporters);

        List<LaneAssignment> assignments = lanes.stream()
                .sorted(Comparator.comparing(Lane::getId))
                .map(lane -> new LaneAssignment(lane.getId(), result.assignment.get(lane.getId())))
                .collect(Collectors.toList());

        List<Long> selectedList = result.selectedTransporters.stream()
                .sorted()
                .collect(Collectors.toList());

        return new AssignmentResponse("success", result.totalCost, assignments, selectedList);
    }
}
