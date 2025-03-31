package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;


}
