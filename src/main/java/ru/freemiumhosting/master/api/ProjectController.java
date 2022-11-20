package ru.freemiumhosting.master.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.freemiumhosting.master.model.Project;
import ru.freemiumhosting.master.service.ProjectService;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    @PostMapping("/api/createProject")
    public String createProject(@ModelAttribute Project project) {
        projectService.createProject(project);
        return "redirect:/projects";
    }

    @PostMapping("/api/updateProject")
    public String updateProject(@ModelAttribute Project project) {
        projectService.updateProject(project);
        return "redirect:/projects";
    }

    @GetMapping("/deploy")
    public String startDeploy(Model model) {
        model.addAttribute("project", new Project());
        return "Deploy";
    }

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "Projects";
    }

    @GetMapping("/projects/{projectId}")
    public String getProjectById(Model model, @PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        return "Project";
    }


    @GetMapping("/")
    public String index(Model model) {
        return "Index";
    }
}
