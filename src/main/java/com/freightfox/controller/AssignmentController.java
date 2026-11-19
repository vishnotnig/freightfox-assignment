package com.freightfox.controller;

import com.freightfox.dto.AssignmentRequest;
import com.freightfox.dto.AssignmentResponse;
import com.freightfox.dto.InputResponse;
import com.freightfox.dto.InputRequest;
import com.freightfox.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transporters")
@Tag(name = "Transporter Assignment", description = "APIs for optimally assigning transporters to freight lanes")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Submit input data", description = "Submit lanes and transporter quotes to be stored for optimization")
    @PostMapping("/input")
    public ResponseEntity<InputResponse> input(@Valid @RequestBody InputRequest request) {
        assignmentService.saveInput(request);
        return ResponseEntity.ok(new InputResponse("success", "Input data saved successfully."));
    }

    @Operation(summary = "Get optimized assignments", description = "Returns optimal transporter-to-lane assignments minimizing total cost within the given transporter limit")
    @PostMapping("/assignment")
    public ResponseEntity<AssignmentResponse> assign(@Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.optimize(request.getMaxTransporters()));
    }
}
