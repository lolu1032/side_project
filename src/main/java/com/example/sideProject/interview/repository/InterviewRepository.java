package com.example.sideProject.interview.repository;

import com.example.sideProject.interview.domain.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
