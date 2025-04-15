package com.tennis.reserve.domain.tennis.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class TimeSlot extends BaseEntity {

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Court court;

    public void update(LocalTime startTime, LocalTime endTime, TimeSlotStatus status) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
