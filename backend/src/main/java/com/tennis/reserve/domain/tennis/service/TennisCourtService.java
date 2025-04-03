package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TennisCourtModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TennisCourtResponse;
import com.tennis.reserve.domain.tennis.dto.response.TennisCourtSimpleResponse;
import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import com.tennis.reserve.domain.tennis.repository.TennisCourtRepository;
import com.tennis.reserve.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TennisCourtService {

    private final TennisCourtRepository tennisCourtRepository;

    @Transactional
    public TennisCourtResponse createTennisCourt(TennisCourtReqForm tennisCourtReqForm) {

        validateTennisCourtName(tennisCourtReqForm.name());

        TennisCourt tennisCourt = TennisCourt.builder()
                .name(tennisCourtReqForm.name())
                .location(tennisCourtReqForm.location())
                .imageUrl(tennisCourtReqForm.imageUrl())
                .build();

        tennisCourtRepository.save(tennisCourt);

        return TennisCourtResponse.fromEntity(tennisCourt);
    }

    private void validateTennisCourtName(String name) {
        boolean isExist = tennisCourtRepository.existsByName(name);

        if (isExist) {
            throw new ServiceException("409-6", "이미 같은 이름의 테니스장이 존재합니다.");
        }
    }


    public TennisCourt findById(Long id) {
        return tennisCourtRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-3", "해당 테니스장은 존재하지 않습니다.")
        );
    }

    public List<TennisCourtResponse> getTennisCourts() {
        return tennisCourtRepository.findAll()
                .stream()
                .map(TennisCourtResponse::fromEntity)
                .toList();
    }

    public TennisCourtResponse getTennisCourt(Long id) {
        return tennisCourtRepository.findById(id)
                .map(TennisCourtResponse::fromEntity)
                .orElseThrow(
                        () -> new ServiceException("404-1", "존재하지 않는 테니스장입니다.")
                );
    }

    @Transactional
    public TennisCourtSimpleResponse modifyTennisCourt(TennisCourtModifyReqForm modifyReqForm, Long id) {
        TennisCourt tennisCourt = tennisCourtRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 테니스장입니다."));

        tennisCourt.update(modifyReqForm.name(), modifyReqForm.location(), modifyReqForm.imageUrl());

        return TennisCourtSimpleResponse.fromEntity(tennisCourt);
    }

    @Transactional
    public void deleteTennisCourt(Long id) {
        TennisCourt tennisCourt = tennisCourtRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 테니스장입니다."));

        tennisCourtRepository.delete(tennisCourt);
    }
}
