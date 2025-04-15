package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot>  findByCourt_TennisCourt_IdAndCourt_Id(Long courtTennisCourtId, Long courtId);
}
