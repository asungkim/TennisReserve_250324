package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TennisCourtResponse;
import com.tennis.reserve.domain.tennis.service.TennisCourtService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tennis-courts")
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;

    @PostMapping
    public RsData<TennisCourtResponse> createTennisCourt(@RequestBody @Valid TennisCourtReqForm tennisCourtReqForm) {
        TennisCourtResponse tennisCourtResponse = tennisCourtService.createTennisCourt(tennisCourtReqForm);

        return new RsData<>(
                "200-3",
                "%s이 등록되었습니다.".formatted(tennisCourtResponse.name()),
                tennisCourtResponse
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    public RsData<List<TennisCourtResponse>> getTennisCourts() {
        List<TennisCourtResponse> tennisCourtList = tennisCourtService.getTennisCourts();

        return new RsData<>(
                "200-4",
                "테니스장 목록을 조회하였습니다.",
                tennisCourtList
        );
    }
}
