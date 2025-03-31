package com.tennis.reserve.domain.tennis.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import com.tennis.reserve.domain.tennis.enums.Environment;
import com.tennis.reserve.domain.tennis.enums.SurfaceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Court extends BaseEntity {

    @Column(nullable = false)
    private String courtCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurfaceType surfaceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private TennisCourt tennisCourt;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TimeSlot> timeSlots = new ArrayList<>();


    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
        timeSlot.setCourt(this);
    }
}
