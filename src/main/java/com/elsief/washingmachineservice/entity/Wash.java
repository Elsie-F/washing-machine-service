package com.elsief.washingmachineservice.entity;

import com.elsief.washingmachineservice.enums.WashStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wash")
public class Wash {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "wash_id_gen")
    @SequenceGenerator(name="wash_id_gen",
            sequenceName="wash_id_seq", allocationSize=1)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appliance_id")
    private Appliance appliance;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;
    @Column
    private LocalDateTime startTime;
    @Column
    private LocalDateTime finishTime;
    @Column
    @Enumerated(EnumType.STRING)
    private WashStatus status;
}
