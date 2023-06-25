package com.mansi.travel.models.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "travel_packages")
@SuperBuilder
public class TravelPackageDao extends BaseDao {
    private String name;
    private Long passengerCapacity;

    @OneToMany(mappedBy = "travelPackage", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TravelDestinationMappingDao> travelDestinationMappings;


}
