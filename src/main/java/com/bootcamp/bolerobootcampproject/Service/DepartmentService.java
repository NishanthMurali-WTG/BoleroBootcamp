package com.bootcamp.bolerobootcampproject.Service;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import com.bootcamp.bolerobootcampproject.Exceptions.BusinessLogicException;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

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

    public Department updateDepartment(Integer id, Department department) {
        Department existingDepartment = getDepartmentById(id);

        if (existingDepartment.isReadonly()) {
            throw new BusinessLogicException("Cannot update readonly department");
        }

        existingDepartment.setName(department.getName());
        existingDepartment.setReadonly(department.isReadonly());
        existingDepartment.setMandatory(department.isMandatory());

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(Integer id) {
        Department department = getDepartmentById(id);
        if(department.isReadonly() || department.isMandatory()) {
            throw new BusinessLogicException("Cannot delete readonly department");
        }
        departmentRepository.delete(department);
    }
}
