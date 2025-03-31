package com.tennis.reserve.domain.tennisCourt.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import com.tennis.reserve.domain.tennisCourt.enums.Environment;
import com.tennis.reserve.domain.tennisCourt.enums.SurfaceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private SurfaceType surfaceType;

    @Enumerated(EnumType.STRING)
    private Environment environment;

    @ManyToOne(fetch = FetchType.LAZY)
    private TennisCourt tennisCourt;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();


}
