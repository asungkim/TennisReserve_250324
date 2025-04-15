package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.CourtModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.court.CourtListResponse;
import com.tennis.reserve.domain.tennis.dto.response.court.CourtResponse;
import com.tennis.reserve.domain.tennis.service.CourtService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tennis-courts/{tennisCourtId}/courts")
public class CourtController {

    private final CourtService courtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<CourtResponse> createCourt(
            @PathVariable Long tennisCourtId,
            @RequestBody @Valid CourtReqForm courtReqForm) {
        CourtResponse courtResponse = courtService.createCourt(courtReqForm, tennisCourtId);

        return new RsData<>(
                "200-3",
                "%s의 %s 코트가 등록되었습니다"
                        .formatted(courtResponse.tennisCourtName(),courtResponse.courtCode()),
                courtResponse
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public RsData<CourtListResponse> getCourts(@PathVariable Long tennisCourtId) {
        CourtListResponse courtResWithList = courtService.getCourts(tennisCourtId);

        return new RsData<>(
                "200-4",
                "%s의 코트 목록을 조회하였습니다."
                        .formatted(courtResWithList.tennisCourtName()),
                courtResWithList
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public RsData<CourtResponse> getCourt(
            @PathVariable Long tennisCourtId,
            @PathVariable Long id) {
        CourtResponse courtResponse = courtService.getCourt(tennisCourtId, id);

        return new RsData<>(
                "200-5",
                "%s 의 %s 코트를 조회하였습니다."
                        .formatted(courtResponse.tennisCourtName(), courtResponse.courtCode()),
                courtResponse
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<CourtResponse> modifyTennisCourt(
            @RequestBody @Valid CourtModifyReqForm modifyReqForm,
            @PathVariable Long tennisCourtId,
            @PathVariable Long id
    ) {
        CourtResponse courtResponse = courtService.modifyCourt(modifyReqForm, tennisCourtId, id);

        return new RsData<>(
                "200-6",
                "%s 의 %s 코트를 수정하였습니다."
                        .formatted(courtResponse.tennisCourtName(), courtResponse.courtCode()),
                courtResponse
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<Void> deleteTennisCourt(
            @PathVariable Long tennisCourtId,
            @PathVariable Long id) {
        String msg = courtService.deleteCourt(tennisCourtId, id);

        return new RsData<>(
                "200-7",
                "%s 코트를 삭제하였습니다.".formatted(msg)
        );
    }
}
