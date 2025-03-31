package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
}
