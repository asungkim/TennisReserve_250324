package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.CourtModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.entity.Court;
import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import com.tennis.reserve.domain.tennis.enums.Environment;
import com.tennis.reserve.domain.tennis.enums.SurfaceType;
import com.tennis.reserve.domain.tennis.repository.CourtRepository;
import com.tennis.reserve.global.exception.ServiceException;
import com.tennis.reserve.global.standard.util.EnumConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;
    private final TennisCourtService tennisCourtService;

    // TODO : 코트 등록, 수정, 삭제

    @Transactional
    public CourtResponse createCourt(CourtReqForm courtReqForm, Long tennisCourtId) {

        // 검증 -> 해당 테니스장의 이미 같은 courtCode가 있는지 검증
        validateDuplicateCourtCode(courtReqForm.courtCode(), tennisCourtId);

        // 테니스장 존재 검증 및 정보 가져오기
        TennisCourt tennisCourt = tennisCourtService.findById(tennisCourtId);

        // courtReqForm에서 String 으로 받은 값을 enum으로 변경
        SurfaceType surfaceType = EnumConvertUtil.convertOrThrow(
                courtReqForm.surfaceType(),
                SurfaceType.class,
                "유효하지 않은 SurfaceType입니다."
        );

        Environment environment = EnumConvertUtil.convertOrThrow(
                courtReqForm.environment(),
                Environment.class,
                "유효하지 않은 Environment입니다."
        );

        Court court = Court.builder()
                .courtCode(courtReqForm.courtCode())
                .environment(environment)
                .surfaceType(surfaceType)
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

    public List<CourtResponse> getCourts(Long tennisCourtId) {
        List<Court> courts = courtRepository.findByTennisCourtId(tennisCourtId).orElseThrow(
                () -> new ServiceException("404-1", "해당 테니스장을 찾을 수 없습니다.")
        );

        return courts.stream().map(CourtResponse::fromEntity).toList();
    }

    public CourtResponse getCourt(Long tennisCourtId, Long id) {
        Court court = courtRepository.findByTennisCourtIdAndId(tennisCourtId, id).orElseThrow(
                () -> new ServiceException("404-1", "해당 테니스장 또는 코트를 찾을 수 없습니다.")
        );

        return CourtResponse.fromEntity(court);
    }

    @Transactional
    public CourtResponse modifyCourt(
            CourtModifyReqForm modifyReqForm,
            Long tennisCourtId,
            Long id) {
        Court court = courtRepository.findByTennisCourtIdAndId(tennisCourtId, id).orElseThrow(
                () -> new ServiceException("404-1", "해당 테니스장 또는 코트를 찾을 수 없습니다.")
        );

        SurfaceType surfaceType = EnumConvertUtil.convertOrThrow(
                modifyReqForm.surfaceType(),
                SurfaceType.class,
                "유효하지 않은 SurfaceType입니다."
        );

        Environment environment = EnumConvertUtil.convertOrThrow(
                modifyReqForm.environment(),
                Environment.class,
                "유효하지 않은 Environment입니다."
        );

        court.update(modifyReqForm.courtCode(), environment, surfaceType);

        return CourtResponse.fromEntity(court);
    }

    @Transactional
    public String deleteCourt(Long tennisCourtId, Long id) {
        Court court = courtRepository.findByTennisCourtIdAndId(tennisCourtId, id).orElseThrow(
                () -> new ServiceException("404-1", "해당 테니스장 또는 코트를 찾을 수 없습니다.")
        );

        String courtCode = court.getCourtCode();
        String tennisCourtName = court.getTennisCourt().getName();

        courtRepository.delete(court);

        return "%s 의 %s".formatted(tennisCourtName, courtCode);
    }
}
