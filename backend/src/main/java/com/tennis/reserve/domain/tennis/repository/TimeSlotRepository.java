package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

}
