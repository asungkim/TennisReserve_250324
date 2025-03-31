package com.tennis.reserve.domain.tennis.repository;

import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TennisCourtRepository extends JpaRepository<TennisCourt, Long> {
    boolean existsByName(String name);
}
