package com.elsief.washingmachineservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "program")
public class Program {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "program_id_gen")
    @SequenceGenerator(name="program_id_gen",
            sequenceName="program_id_seq", allocationSize=1)
    private long id;
    @Column
    private String name;
    @Column
    private long durationInMinutes;
    @Column
    private int temperature;
    @Column
    private int spinSpeed;
}
