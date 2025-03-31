package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.service.CourtService;
import com.tennis.reserve.domain.tennis.service.TennisCourtService;
import com.tennis.reserve.domain.tennis.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;
    private final CourtService courtService;
    private final TimeSlotService timeSlotService;


}
