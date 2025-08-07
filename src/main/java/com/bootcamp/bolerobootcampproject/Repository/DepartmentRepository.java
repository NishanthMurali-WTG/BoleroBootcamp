package com.bootcamp.bolerobootcampproject.Repository;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findByMandatory(boolean mandatory);
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
}
