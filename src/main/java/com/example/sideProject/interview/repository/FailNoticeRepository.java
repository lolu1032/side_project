package com.example.sideProject.interview.repository;

import com.example.sideProject.interview.domain.FailNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailNoticeRepository extends JpaRepository<FailNotice, Long> {
}
