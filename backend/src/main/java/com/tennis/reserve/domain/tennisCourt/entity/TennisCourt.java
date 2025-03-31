package com.tennis.reserve.domain.tennisCourt.entity;

import com.tennis.reserve.domain.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class TennisCourt extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "tennisCourt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Court> courts = new ArrayList<>();

}
