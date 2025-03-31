package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.repository.TennisCourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TennisCourtService {

    private final TennisCourtRepository tennisCourtRepository;
}
