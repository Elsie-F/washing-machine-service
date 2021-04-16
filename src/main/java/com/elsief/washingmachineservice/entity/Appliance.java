package com.elsief.washingmachineservice.entity;

import com.elsief.washingmachineservice.enums.ApplianceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appliance")
public class Appliance {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "appliance_id_gen")
    @SequenceGenerator(name="appliance_id_gen",
            sequenceName="appliance_id_seq", allocationSize=1)
    private long id;
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private ApplianceStatus status;
}
