package com.bootcamp.bolerobootcampproject.ServiceImplementation;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import com.bootcamp.bolerobootcampproject.Entity.Employee;
import com.bootcamp.bolerobootcampproject.Exceptions.BusinessLogicException;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import com.bootcamp.bolerobootcampproject.Repository.EmployeeRepository;
import com.bootcamp.bolerobootcampproject.Service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id : " + id));
    }

    @Transactional
    public Department saveDepartment(Department department) {
        String trimmedName = department.getName().trim();
        if (departmentRepository.existsByName(trimmedName)) {
            throw new BusinessLogicException("A department with the name '" + department.getName() + "' already exists.");
        }
        department.setName(trimmedName);
        Department savedDepartment = departmentRepository.save(department);
        if (savedDepartment.isMandatory()){
            addDepartmentToEmployees(savedDepartment);
        }
        return savedDepartment;
    }

    @Transactional
    public Department updateDepartment(Integer id, Department department) {
        Department existingDepartment = getDepartmentById(id);

        if (existingDepartment.isReadonly()) {
            throw new BusinessLogicException("Cannot update readonly department");
        }
        String trimmedName = department.getName().trim();
        Optional<Department> optionalDepartment = departmentRepository.findByName(trimmedName);
        if (optionalDepartment.isPresent() && !optionalDepartment.get().getId().equals(id)) {
            throw new BusinessLogicException("A department with the name '" + department.getName() + "' already exists.");
        }

        boolean isBecomingMandatory = !existingDepartment.isMandatory() && department.isMandatory();
        boolean isLosingMandatory = existingDepartment.isMandatory() && (!department.isMandatory());

        existingDepartment.setName(department.getName());
        existingDepartment.setReadonly(department.isReadonly());
        existingDepartment.setMandatory(department.isMandatory());

        if (isBecomingMandatory){
            addDepartmentToEmployees(existingDepartment);
        }
        if(isLosingMandatory){
            removeDepartment(existingDepartment);
        }

        return existingDepartment;
    }

    public void deleteDepartment(Integer id) {
        Department department = getDepartmentById(id);
        if(department.isReadonly()) {
            throw new BusinessLogicException("Cannot delete readonly department");
        }
        departmentRepository.delete(department);
    }

    private void removeDepartment(Department department) {
        List<Employee> employeeInDepartment = employeeRepository.findByDepartments(department);

        for (Employee employee : employeeInDepartment) {
            employee.getDepartments().removeIf(d -> d.getId().equals(department.getId()));
        }
    }

    private void addDepartmentToEmployees(Department department) {
        List<Employee> allEmployees = employeeRepository.findAll();
        for (Employee employee : allEmployees) {
            if (employee.getDepartments().stream().noneMatch(d -> d.getId().equals(department.getId()))) {
                employee.getDepartments().add(department);
            }
        }
    }
}