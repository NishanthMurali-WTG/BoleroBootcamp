package com.bootcamp.bolerobootcampproject.Repository;

import com.bootcamp.bolerobootcampproject.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
