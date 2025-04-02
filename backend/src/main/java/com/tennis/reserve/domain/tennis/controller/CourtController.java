package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.service.CourtService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courts")
public class CourtController {

    private final CourtService courtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<CourtResponse> createCourt(@RequestBody @Valid CourtReqForm courtReqForm) {
        CourtResponse courtResponse = courtService.createCourt(courtReqForm);

        return new RsData<>(
                "200-3",
                "코트가 등록되었습니다",
                courtResponse
        );
    }
}
