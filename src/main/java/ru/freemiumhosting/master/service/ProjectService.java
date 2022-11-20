package ru.freemiumhosting.master.service;

import io.kubernetes.client.openapi.ApiException;
import org.springframework.stereotype.Service;
import ru.freemiumhosting.master.model.Project;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    void createProject(Project project);
    void deployProject(Project project);
    void updateProject(Project project);
    Project getProjectDetails(Long projectId);
    List<Project> getAllProjects();
    Project findProjectById(Long projectId);
    void generateProjectNodePort(Project project);
}
