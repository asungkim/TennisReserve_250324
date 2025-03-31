package com.tennis.reserve.domain.tennis.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import com.tennis.reserve.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TennisCourtManager extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private TennisCourt tennisCourt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member manager;

}
