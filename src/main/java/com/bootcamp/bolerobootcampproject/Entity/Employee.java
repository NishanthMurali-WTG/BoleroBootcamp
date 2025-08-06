package com.bootcamp.bolerobootcampproject.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "EMPLOYEE")
@RequiredArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_employee")
    @SequenceGenerator(name = "seq_employee", sequenceName = "SEQ_EMPLOYEE", allocationSize = 1)
    private Integer id;

    @Column(name = "NAMEFIRST")
    private String nameFirst;

    @Column(name = "NAMELAST")
    private String nameLast;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "MAP_EMPLOYEE_DEPARTMENT",
            joinColumns = @JoinColumn(name = "ID_EMPLOYEE"),
            inverseJoinColumns = @JoinColumn(name = "ID_DEPARTMENT")
    )
    private Set<Department> departments = new HashSet<>();
}
