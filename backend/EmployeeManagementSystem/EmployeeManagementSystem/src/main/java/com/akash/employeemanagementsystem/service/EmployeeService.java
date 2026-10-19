
package com.akash.employeemanagementsystem.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.akash.employeemanagementsystem.entity.Employee;
import com.akash.employeemanagementsystem.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee saveEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists");
        }

        employee.setPassword(
                passwordEncoder.encode(employee.getPassword()));

        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);

        employeeRepository.findByEmail(employee.getEmail())
                .ifPresent(found -> {
                    if (!found.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "Email already exists");
                    }
                });

        existing.setName(employee.getName());
        existing.setEmail(employee.getEmail());

        if (employee.getPassword() != null
                && !employee.getPassword().isBlank()) {
            existing.setPassword(
                    passwordEncoder.encode(employee.getPassword()));
        }

        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    // Search employees by name or email
    public List<Employee> searchEmployees(String keyword) {
        return employeeRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword);
    }

    // Pagination and sorting
    public Page<Employee> getEmployeesWithPagination(
            int page, int size, String sortBy, String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be 0 or greater and size must be between 1 and 100");
        }

        if (!"id".equals(sortBy)
                && !"name".equals(sortBy)
                && !"email".equals(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "sortBy must be id, name, or email");
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeRepository.findAll(pageable);
    }
}
