package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TennisCourtModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.tennisCourt.TennisCourtItem;
import com.tennis.reserve.domain.tennis.dto.response.tennisCourt.TennisCourtResponse;
import com.tennis.reserve.domain.tennis.service.TennisCourtService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tennis-courts")
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TennisCourtResponse> createTennisCourt(@RequestBody @Valid TennisCourtReqForm tennisCourtReqForm) {
        TennisCourtResponse tennisCourtResponse = tennisCourtService.createTennisCourt(tennisCourtReqForm);

        return new RsData<>(
                "200-3",
                "%s이 등록되었습니다.".formatted(tennisCourtResponse.name()),
                tennisCourtResponse
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public RsData<List<TennisCourtResponse>> getTennisCourts() {
        List<TennisCourtResponse> tennisCourtList = tennisCourtService.getTennisCourts();

        return new RsData<>(
                "200-4",
                "테니스장 목록을 조회하였습니다.",
                tennisCourtList
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public RsData<TennisCourtResponse> getTennisCourt(@PathVariable Long id) {
        TennisCourtResponse tennisCourtResponse = tennisCourtService.getTennisCourt(id);

        return new RsData<>(
                "200-5",
                "%s 조회하였습니다.".formatted(tennisCourtResponse.name()),
                tennisCourtResponse
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TennisCourtItem> modifyTennisCourt(
            @RequestBody @Valid TennisCourtModifyReqForm modifyReqForm,
            @PathVariable Long id
    ) {
        TennisCourtItem tennisCourtResponse = tennisCourtService.modifyTennisCourt(modifyReqForm, id);

        return new RsData<>(
                "200-6",
                "%s 수정하였습니다.".formatted(tennisCourtResponse.name()),
                tennisCourtResponse
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<Void> deleteTennisCourt(@PathVariable Long id) {
        tennisCourtService.deleteTennisCourt(id);

        return new RsData<>(
                "200-7",
                "해당 테니스장을 삭제하였습니다."
        );
    }

}
