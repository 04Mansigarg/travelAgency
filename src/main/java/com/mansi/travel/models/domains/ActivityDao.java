package com.mansi.travel.models.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "activities")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ActivityDao extends BaseDao{
    private String name;

    @Column(columnDefinition="TEXT")
    private String description; //  free text
    private Double cost;
    private Long capacity;
    private Long consumption;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private DestinationDao destination;
}
