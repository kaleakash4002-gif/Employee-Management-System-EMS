
package com.akash.employeemanagementsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.akash.employeemanagementsystem.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email);

    Page<Employee> findAll(Pageable pageable);
}
