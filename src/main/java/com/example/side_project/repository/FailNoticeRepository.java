package com.example.side_project.repository;

import com.example.side_project.domain.FailNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailNoticeRepository extends JpaRepository<FailNotice, Long> {
}
