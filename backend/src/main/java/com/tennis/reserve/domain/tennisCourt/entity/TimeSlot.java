package com.tennis.reserve.domain.tennisCourt.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import com.tennis.reserve.domain.tennisCourt.enums.TimeSlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class TimeSlot extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private TimeSlotStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Court court;
}
