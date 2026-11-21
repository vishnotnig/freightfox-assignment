package com.freightfox.service;

import com.freightfox.dto.AssignmentResponse;
import com.freightfox.dto.InputRequest;

public interface AssignmentService {
    void saveInput(InputRequest request);
    AssignmentResponse optimize(int maxTransporters);
}
