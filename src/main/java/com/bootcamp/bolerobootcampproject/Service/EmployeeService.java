package com.bootcamp.bolerobootcampproject.Service;

import com.bootcamp.bolerobootcampproject.Entity.Employee;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import com.bootcamp.bolerobootcampproject.Repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private static final String MANDATORY_DEPT_NAME = "Organisation";

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id : "));
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Integer id, Employee employee) {
        Employee existingEmployee = getEmployeeById(id);

        existingEmployee.setNameFirst(employee.getNameFirst());
        existingEmployee.setNameLast(employee.getNameLast());
        existingEmployee.setDepartments(employee.getDepartments());

        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Integer id) {
        if(!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id : " + id);
        }
        employeeRepository.deleteById(id);
    }
}
