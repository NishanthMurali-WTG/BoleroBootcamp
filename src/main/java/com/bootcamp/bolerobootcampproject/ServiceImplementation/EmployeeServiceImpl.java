package com.bootcamp.bolerobootcampproject.ServiceImplementation;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import com.bootcamp.bolerobootcampproject.Entity.Employee;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import com.bootcamp.bolerobootcampproject.Repository.EmployeeRepository;
import com.bootcamp.bolerobootcampproject.Service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id : "));
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        Set<Department> requestedDepartments = fetchDepartmentsFromIds(employee.getDepartments());
        employee.setDepartments(requestedDepartments);
        ensureMandatoryDepartment(employee);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Integer id, Employee employee) {
        Employee existingEmployee = getEmployeeById(id);

        Set<Department> requestedDepartments = fetchDepartmentsFromIds(employee.getDepartments());
        existingEmployee.setNameFirst(employee.getNameFirst());
        existingEmployee.setNameLast(employee.getNameLast());
        existingEmployee.setDepartments(requestedDepartments);
        ensureMandatoryDepartment(existingEmployee);
        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Integer id) {
        if(!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id : " + id);
        }
        employeeRepository.deleteById(id);
    }

    private void ensureMandatoryDepartment(Employee employee) {
        List<Department> mandatoryDepartments = departmentRepository.findByMandatory(true);
        if (mandatoryDepartments.isEmpty()) {
            return;
        }
        for (Department department : mandatoryDepartments) {
            if (employee.getDepartments().stream().noneMatch(d -> d.getId().equals(department.getId()))) {
                employee.getDepartments().add(department);
            }
        }
    }

    private Set<Department> fetchDepartmentsFromIds(Set<Department> departments) {
        if(departments == null || departments.isEmpty()) {
            return new HashSet<>();
        }
        List<Integer> deptIds = departments.stream()
                .map(Department::getId)
                .toList();
        return new HashSet<>(departmentRepository.findAllById(deptIds));
    }
}