package com.bootcamp.bolerobootcampproject.EmployeeServiceTests;

import com.bootcamp.bolerobootcampproject.Entity.Department;
import com.bootcamp.bolerobootcampproject.Entity.Employee;
import com.bootcamp.bolerobootcampproject.Exceptions.ResourceNotFoundException;
import com.bootcamp.bolerobootcampproject.Repository.DepartmentRepository;
import com.bootcamp.bolerobootcampproject.Repository.EmployeeRepository;
import com.bootcamp.bolerobootcampproject.ServiceImplementation.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest{

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void getAllEmployees_shouldReturnAllEmployees() {
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setNameFirst("Nishanth");
        employee1.setNameLast("Murali");
        employee1.setDepartments(new HashSet<>());

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setNameFirst("Arun");
        employee2.setNameLast("P R");
        employee2.setDepartments(new HashSet<>());

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
        List<Employee> employees = employeeService.getAllEmployees();

        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertEquals("Arun", employees.get(1).getNameFirst());
        verify(employeeRepository, times(1)).findAll();

    }
    @Test
    void saveEmployee_AddsMandatoryDepartmentAndSaveEmployeeSuccessfully(){
        Employee employee = new Employee();
        employee.setNameFirst("Nishanth");
        employee.setNameLast("Murali");
        Department department = new Department();
        department.setId(2);
        employee.setDepartments(new HashSet<>(Set.of(department)));

        Department OrgDepartment = new Department();
        OrgDepartment.setId(1);
        OrgDepartment.setName("Organisation");
        OrgDepartment.setReadonly(true);
        OrgDepartment.setMandatory(true);

        Department HRdepartment = new Department();
        HRdepartment.setId(2);
        HRdepartment.setName("HR");
        HRdepartment.setReadonly(false);
        HRdepartment.setMandatory(false);

        when(departmentRepository.findAllById(List.of(2))).thenReturn(List.of(HRdepartment));
        when(departmentRepository.findByMandatory(true)).thenReturn(List.of(OrgDepartment));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee savedEmployee = employeeService.saveEmployee(employee);
        assertNotNull(savedEmployee);

        assertEquals(2, savedEmployee.getDepartments().size());
        assertTrue(savedEmployee.getDepartments().stream()
                .anyMatch(d -> d.getName().equals("Organisation") && d.isMandatory()));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_whenEmployeeExists_shouldReturnEmployee(){
        Employee employee = new Employee();
        employee.setId(1);
        employee.setNameFirst("Nishanth");
        employee.setNameLast("Murali");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        Employee savedEmployee = employeeService.getEmployeeById(1);

        assertNotNull(savedEmployee);
        assertEquals(employee, savedEmployee);
        assertEquals(1, savedEmployee.getId());
        assertEquals("Nishanth", savedEmployee.getNameFirst());
    }

    @Test
    void getEmployeeById_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> { employeeService.getEmployeeById(99); });
    }

    @Test
    void updateEmployee_whenEmployeeExists_shouldUpdateSuccessfully(){
        Employee employee = new Employee();
        employee.setId(1);
        employee.setNameFirst("Nishanth");
        employee.setNameLast("Murali");

        Employee employeeDetailsToUpdate = new Employee();
        employeeDetailsToUpdate.setNameFirst("Arun");
        employeeDetailsToUpdate.setNameLast("P R");
        employeeDetailsToUpdate.setDepartments(new HashSet<>());

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByMandatory(true)).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee updtedEmployee = employeeService.updateEmployee(1, employeeDetailsToUpdate);

        assertNotNull(updtedEmployee);
        assertEquals(employee, updtedEmployee);
        assertEquals("Arun", updtedEmployee.getNameFirst());
        assertEquals("P R", updtedEmployee.getNameLast());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_whenEmployeeExists_shouldCallDelete(){
        when(employeeRepository.existsById(1)).thenReturn(true);

        doNothing().when(employeeRepository).deleteById(1);
        employeeService.deleteEmployee(1);
        verify(employeeRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteEmployee_whenEmployeeDoesNotExist_shouldThrowResourceNotFoundException(){
        when(employeeRepository.existsById(1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> { employeeService.deleteEmployee(1); });
    }
}
