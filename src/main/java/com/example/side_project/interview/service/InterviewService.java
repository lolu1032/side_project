package com.example.side_project.interview.service;

import com.example.side_project.interview.domain.FailNotice;
import com.example.side_project.interview.domain.Interview;
import com.example.side_project.interview.repository.FailNoticeRepository;
import com.example.side_project.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository repository;
    private final FailNoticeRepository failNoticeRepository;
    /**
     * 저장 로직
     */
    public Interview save(Request request) {
        Interview interview = Interview.builder()
                .applicantName(request.applicantName())
                .company(request.company())
                .timeRange(request.timeRange())
                .location(request.location())
                .startDatetime(request.startDatetime())
                .endDatetime(request.endDatetime())
                .note(request.note())
                .build();
        return repository.save(interview);
    }

    public FailNotice failSave(FaileRequest request) {
        FailNotice failNotice = FailNotice.builder()
                .company(request.company())
                .memo(request.note())
                .build();
        return failNoticeRepository.save(failNotice);
    }

}
