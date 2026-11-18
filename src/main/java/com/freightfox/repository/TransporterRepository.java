package com.freightfox.repository;

import com.freightfox.model.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransporterRepository extends JpaRepository<Transporter, Long> {
}
