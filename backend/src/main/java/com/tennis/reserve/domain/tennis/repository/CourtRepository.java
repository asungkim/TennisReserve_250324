package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    Optional<List<Court>> findByTennisCourtId(Long tennisCourtId);

    Optional<Court> findByTennisCourtIdAndId(Long tennisCourtId, Long id);
}
