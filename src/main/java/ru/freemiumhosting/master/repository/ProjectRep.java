package ru.freemiumhosting.master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.freemiumhosting.master.model.Project;


@Repository
public interface ProjectRep extends JpaRepository<Project,Long> {
    Project findProjectById(Long id);
    Boolean existsByNodePort(Integer nodePort);
}
