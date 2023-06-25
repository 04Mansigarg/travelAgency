package com.mansi.travel.models.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "passenger_travel_package_destination_activity_mapping")
@SuperBuilder
public class PassengerActivityDetailsMappingDao extends BaseDao {
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private PassengerDao passenger;
    @ManyToOne
    @JoinColumn(name = "travel_package_id")
    private TravelPackageDao travelPackage;
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private DestinationDao destination;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityDao activity;

    private Double activityCost;
}
