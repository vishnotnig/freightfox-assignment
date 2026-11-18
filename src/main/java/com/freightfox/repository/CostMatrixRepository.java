package com.freightfox.repository;

import com.freightfox.model.CostMatrix;
import com.freightfox.model.Lane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostMatrixRepository extends JpaRepository<CostMatrix, Long> {
    List<CostMatrix> findByLaneOrderByCostAsc(Lane lane);
}
