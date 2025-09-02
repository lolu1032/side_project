package com.example.side_project.interview.repository;

import com.example.side_project.interview.domain.FailNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailNoticeRepository extends JpaRepository<FailNotice, Long> {
}
