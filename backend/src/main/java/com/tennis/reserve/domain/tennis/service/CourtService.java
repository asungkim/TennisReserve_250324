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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;
    private final TennisCourtService tennisCourtService;

    // TODO : 코트 등록, 수정, 삭제

    @Transactional
    public CourtResponse createCourt(CourtReqForm courtReqForm) {

        // 검증 -> 해당 테니스장의 이미 같은 courtCode가 있는지 검증
        validateDuplicateCourtCode(courtReqForm.courtCode(), courtReqForm.tennisCourtId());

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

    private void validateDuplicateCourtCode(String courtCode, Long tennisCourtId) {
        TennisCourt tennisCourt = tennisCourtService.findById(tennisCourtId);
        Optional<Court> opFirst = tennisCourt.getCourts().stream().filter(court -> court.getCourtCode().equals(courtCode)).findFirst();

        if (opFirst.isPresent()) {
            throw new ServiceException("409-1", "해당 테니스장에는 이미 %s 코트가 존재합니다.".formatted(courtCode));
        }
    }

    public Court findById(Long id) {
        return courtRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-2", "해당 코트는 존재하지 않습니다.")
        );
    }
}
