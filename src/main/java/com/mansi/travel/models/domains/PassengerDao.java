package com.mansi.travel.models.domains;

import com.mansi.travel.models.enums.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "passengers")
@SuperBuilder
public class PassengerDao extends BaseDao {
    private String name;

    private String mobile;

    @Enumerated(EnumType.STRING)
    private PassengerType passengerType;

    private Double totalBalance;
    private Double utilizedBalance;
    private Double remainingBalance;

}
