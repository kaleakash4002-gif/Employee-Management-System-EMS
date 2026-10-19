
package com.akash.employeemanagementsystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.akash.employeemanagementsystem.dto.LoginRequest;
import com.akash.employeemanagementsystem.dto.LoginResponse;
import com.akash.employeemanagementsystem.entity.Employee;
import com.akash.employeemanagementsystem.repository.EmployeeRepository;
import com.akash.employeemanagementsystem.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Register a new employee
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody Employee employee) {

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        Employee newEmployee = new Employee();
        newEmployee.setName(employee.getName());
        newEmployee.setEmail(employee.getEmail());
        newEmployee.setPassword(
                passwordEncoder.encode(employee.getPassword()));

        Employee savedEmployee = employeeRepository.save(newEmployee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Employee registered successfully with ID: "
                        + savedEmployee.getId());
    }

    // Login an existing employee
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        Employee employee = employeeRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (employee == null
                || !passwordEncoder.matches(
                        request.getPassword(),
                        employee.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            null, "Invalid email or password"));
        }

        String token = jwtService.generateToken(employee.getEmail());

        return ResponseEntity.ok(
                new LoginResponse(token, "Login successful"));
    }
}
