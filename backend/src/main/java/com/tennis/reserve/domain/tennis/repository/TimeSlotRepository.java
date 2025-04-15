package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot>  findByCourt_TennisCourt_IdAndCourt_Id(Long courtTennisCourtId, Long courtId);

    Optional<TimeSlot> findByCourt_TennisCourt_IdAndCourt_IdAndId(Long courtTennisCourtId, Long courtId, Long id);
}
