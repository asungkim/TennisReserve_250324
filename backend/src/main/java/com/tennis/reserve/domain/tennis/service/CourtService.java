package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.entity.Court;
import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import com.tennis.reserve.domain.tennis.repository.CourtRepository;
import com.tennis.reserve.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;
    private final TennisCourtService tennisCourtService;

    // TODO : 코트 등록, 수정, 삭제

    @Transactional
    public CourtResponse createCourt(CourtReqForm courtReqForm) {

        TennisCourt tennisCourt = tennisCourtService.findById(courtReqForm.tennisCourtId());

        Court court = Court.builder()
                .courtCode(courtReqForm.courtCode())
                .environment(courtReqForm.environment())
                .surfaceType(courtReqForm.surfaceType())
                .tennisCourt(tennisCourt)
                .build();

        tennisCourt.addCourt(court);
        courtRepository.save(court);

        return CourtResponse.fromEntity(court);
    }

    public Court findById(Long id) {
        return courtRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 코트는 존재하지 않습니다.")
        );
    }
}
