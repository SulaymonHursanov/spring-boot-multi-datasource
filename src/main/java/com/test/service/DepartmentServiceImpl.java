package com.test.service;

import com.test.entities.Department;
import com.test.repositoryReadOnly.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @PostConstruct
    @Override
    public List<Department> getDepartments() {
        List<Department> departments = departmentRepository.findAll();
        log.info("departments size: {}", departments.size());
        return departments;
    }
}
