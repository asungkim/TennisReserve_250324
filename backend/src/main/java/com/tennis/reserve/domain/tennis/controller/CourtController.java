package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.service.CourtService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                "코트가 등록되었습니다",
                courtResponse
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public RsData<List<CourtResponse>> getCourts(@PathVariable Long tennisCourtId) {
        List<CourtResponse> courtList = courtService.getCourts(tennisCourtId);

        return new RsData<>(
                "200-4",
                "테니스장 목록을 조회하였습니다.",
                courtList
        );
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public RsData<CourtResponse> getCourt(
            @PathVariable Long tennisCourtId,
            @PathVariable Long id) {
        CourtResponse courtResponse = courtService.getCourt(tennisCourtId,id);

        return new RsData<>(
                "200-5",
                "%s 를 조회하였습니다.".formatted(courtResponse.courtCode()),
                courtResponse
        );
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public RsData<TennisCourtSimpleResponse> modifyTennisCourt(
//            @RequestBody @Valid TennisCourtModifyReqForm modifyReqForm,
//            @PathVariable Long id
//    ) {
//        TennisCourtSimpleResponse tennisCourtResponse = tennisCourtService.modifyTennisCourt(modifyReqForm, id);
//
//        return new RsData<>(
//                "200-6",
//                "%s 수정하였습니다.".formatted(tennisCourtResponse.name()),
//                tennisCourtResponse
//        );
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public RsData<Void> deleteTennisCourt(@PathVariable Long id) {
//        tennisCourtService.deleteTennisCourt(id);
//
//        return new RsData<>(
//                "200-7",
//                "해당 테니스장을 삭제하였습니다."
//        );
//    }
}
