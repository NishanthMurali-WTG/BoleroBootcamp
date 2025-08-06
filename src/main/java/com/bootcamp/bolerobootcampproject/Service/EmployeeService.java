package com.bootcamp.bolerobootcampproject.Service;

import com.bootcamp.bolerobootcampproject.Entity.Employee;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Integer id);
    Employee saveEmployee(Employee employee);
    Employee updateEmployee(Integer id, Employee employee);
    void deleteEmployee(Integer id);
}