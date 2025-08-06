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

    public Department saveDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new BusinessLogicException("A department with the name '" + department.getName() + "' already exists.");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Integer id, Department department) {
        Department existingDepartment = getDepartmentById(id);

        if (existingDepartment.isReadonly()) {
            throw new BusinessLogicException("Cannot update readonly department");
        }

        boolean isMandatoryChanging = existingDepartment.isMandatory() && (!department.isMandatory());

        existingDepartment.setName(department.getName());
        existingDepartment.setReadonly(department.isReadonly());
        existingDepartment.setMandatory(department.isMandatory());

        Department updatedDepartment = departmentRepository.save(existingDepartment);

        if(isMandatoryChanging){
            removeDepartment(updatedDepartment);
        }

        return updatedDepartment;
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
            employeeRepository.save(employee);
        }
    }
}