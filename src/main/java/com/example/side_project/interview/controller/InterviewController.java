package com.example.side_project.interview.controller;

import com.example.side_project.interview.domain.FailNotice;
import com.example.side_project.interview.domain.Interview;
import com.example.side_project.interview.repository.FailNoticeRepository;
import com.example.side_project.interview.repository.InterviewRepository;
import com.example.side_project.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService service;
    private final InterviewRepository repository;
    private final FailNoticeRepository failNoticeRepository;
    /**
     * 저장 로직
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Request request) {
        service.save(request);

        return ResponseEntity.ok("저장완료했습니다.");
    }
    @PostMapping("/fail/save")
    public ResponseEntity<?> failSave(@RequestBody FaileRequest request) {
        service.failSave(request);
        return ResponseEntity.ok("저장완료했습니다.");
    }

    /**
     * List로 보여주기용
     */
    @GetMapping
    public List<Interview> list() {
        return repository.findAll();
    }

    @GetMapping("/fail")
    public List<FailNotice> fail() {
        return failNoticeRepository.findAll();
    }
}
