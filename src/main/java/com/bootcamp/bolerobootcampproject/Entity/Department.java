package com.bootcamp.bolerobootcampproject.Entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "DEPARTMENT")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_department")
    @SequenceGenerator(name = "seq_department", sequenceName = "SEQ_DEPARTMENT", allocationSize = 1)
    private Integer id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "READONLY")
    private boolean readonly;

    @Column(name = "MANDATORY")
    private boolean mandatory;
}
