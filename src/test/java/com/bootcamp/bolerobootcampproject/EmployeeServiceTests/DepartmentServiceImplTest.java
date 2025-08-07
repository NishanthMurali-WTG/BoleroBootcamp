package com.bootcamp.bolerobootcampproject.EmployeeServiceTests;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import com.bootcamp.bolerobootcampproject.Entity.Employee;
import com.bootcamp.bolerobootcampproject.Exceptions.BusinessLogicException;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import com.bootcamp.bolerobootcampproject.Repository.EmployeeRepository;
import com.bootcamp.bolerobootcampproject.ServiceImplementation.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;

    @Test
    void getAllDepartments_shouldReturnAllDepartments() {
        Department department1 = new Department();
        department1.setId(1);
        department1.setName("Organisation");
        department1.setReadonly(true);
        department1.setMandatory(true);

        Department department2 = new Department();
        department2.setId(2);
        department2.setName("HR");
        department2.setReadonly(false);
        department2.setMandatory(false);

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(department1, department2));
        List<Department> departments = departmentServiceImpl.getAllDepartments();

        assertNotNull(departments);
        assertEquals(2, departments.size());
        assertTrue(department1.isMandatory());
        verify((departmentRepository), times(1)).findAll();
    }

    @Test
    void getDepartmentById_whenDepartmentExists_shouldReturnDepartment(){
        Department department = new Department();
        department.setId(2);
        department.setName("HR");

        when(departmentRepository.findById(2)).thenReturn(Optional.of(department));
        Department returnedDepartment = departmentServiceImpl.getDepartmentById(2);

        assertNotNull(returnedDepartment);
        assertEquals(2, returnedDepartment.getId());
        assertEquals("HR", returnedDepartment.getName());
    }

    @Test
    void getDepartmentById_whenDepartmentDoesNotExist_shouldThrowResourceNotFoundException(){
        when(departmentRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentServiceImpl.getDepartmentById(2));
    }

    @Test
    void saveDepartment_whenNotMandatory_shouldSaveSuccessfully() {
        Department newDepartment = new Department();
        newDepartment.setName("New Department");
        newDepartment.setMandatory(false);

        when(departmentRepository.existsByName("New Department")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);

        Department savedDepartment = departmentServiceImpl.saveDepartment(newDepartment);

        assertNotNull(savedDepartment);
        assertFalse(savedDepartment.isMandatory());
        verify(departmentRepository, times(1)).save(newDepartment);

        verify(employeeRepository, never()).findAll();
    }

    @Test
    void saveDepartment_whenIsMandatory_shouldSaveAndAddAllEmployees() {
        Department newDepartment = new Department();
        newDepartment.setName("New Mandatory Dept");
        newDepartment.setMandatory(true);

        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);
        when(employeeRepository.findAll()).thenReturn(List.of(new Employee(), new Employee()));

        departmentServiceImpl.saveDepartment(newDepartment);

        verify(departmentRepository, times(1)).save(newDepartment);
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void saveDepartment_whenNameExists_shouldThrowBusinessLogicException() {
        Department newDepartment = new Department();
        newDepartment.setName("Existing Department");
        when(departmentRepository.existsByName("Existing Department")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> {
            departmentServiceImpl.saveDepartment(newDepartment);
        });
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_whenReadOnly_shouldThrowBusinessLogicException() {
        Department readOnlyDepartment = new Department();
        readOnlyDepartment.setId(1);
        readOnlyDepartment.setReadonly(true);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(readOnlyDepartment));

        assertThrows(BusinessLogicException.class, () -> {
            departmentServiceImpl.updateDepartment(1, new Department());
        });
    }

    @Test
    void deleteDepartment_whenReadOnly_shouldThrowBusinessLogicException() {
        Department readOnlyDepartment = new Department();
        readOnlyDepartment.setId(1);
        readOnlyDepartment.setReadonly(true);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(readOnlyDepartment));
        
        assertThrows(BusinessLogicException.class, () -> {
            departmentServiceImpl.deleteDepartment(1);
        });
    }

    @Test
    void deleteDepartment_whenNotReadOnly_shouldCallDeleteSuccessfully() {
        Department notReadOnlyDepartment = new Department();
        notReadOnlyDepartment.setId(1);
        notReadOnlyDepartment.setReadonly(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(notReadOnlyDepartment));
        doNothing().when(departmentRepository).delete(any(Department.class));

        departmentServiceImpl.deleteDepartment(1);

        verify(departmentRepository, times(1)).delete(notReadOnlyDepartment);
    }

}
