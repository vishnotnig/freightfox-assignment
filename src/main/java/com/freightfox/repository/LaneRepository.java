package com.freightfox.repository;

import com.freightfox.model.Lane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaneRepository extends JpaRepository<Lane, Long> {
}
