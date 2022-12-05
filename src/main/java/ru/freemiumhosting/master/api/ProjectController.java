package ru.freemiumhosting.master.api;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.freemiumhosting.master.dto.ProjectDto;
import ru.freemiumhosting.master.dto.ProjectMapper;
import ru.freemiumhosting.master.exception.DeployException;
import ru.freemiumhosting.master.model.Project;
import ru.freemiumhosting.master.service.ProjectService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectMapper projectMapper;
    private final ProjectService projectService;


    @PostMapping("/api/createProject")
    public String createProject(@ModelAttribute ProjectDto dto) {
        String errorMessage = null;
        var project = projectMapper.toEntity(dto);
//        log.info("Envs {}", dto.getEnvs()); //TODO: use envs
//        dto.getEnvNames().stream().z
        try {
            projectService.createProject(project);
        } catch (Exception deployException) {
            log.error("Error executing request", deployException);
            errorMessage = deployException.getMessage();
        }
        return errorMessage == null ? "redirect:/projects" : MessageFormat.format(
                "redirect:/deploy/?errorMessage={1}", project.getId(), URLEncoder.encode(errorMessage));
    }

    @PostMapping("/api/deploy")
    public String deployProject(@ModelAttribute ProjectDto dto) throws DeployException {
        projectService.deployProject(projectMapper.toEntity(dto));
        return "Деплой проекта запущен";
    }

    @PostMapping("/api/updateProject")
    public String updateProject(@ModelAttribute ProjectDto dto) {
        String errorMessage = null;
        var project = projectMapper.toEntity(dto); //TODO: need delete ALL previous envs and persist new ones
        try {
            projectService.updateProject(project);
        } catch (Exception deployException) {
            log.error("Error executing request", deployException);
            errorMessage = deployException.getMessage();
        }
        return errorMessage == null ? "redirect:/projects" : MessageFormat.format(
                "redirect:/deploy/?errorMessage={1}", project.getId(), URLEncoder.encode(errorMessage));
    }

    @GetMapping("/projects/delete/{projectId}")
    public String deleteProject(@PathVariable Long projectId) {
        String errorMessage = null;
        try {
            Project project = projectService.findProjectById(projectId);
            projectService.deleteProject(project);
        } catch (Exception e) {
            log.error("Error executing request", e);
            errorMessage = e.getMessage();
        }
        return errorMessage == null ? "redirect:/projects" : MessageFormat.format(
                "redirect:/projects?errorMessage={0}", URLEncoder.encode(errorMessage));
    }

    @GetMapping("/deploy")
    public String startDeploy(Model model, @RequestParam(required = false) String errorMessage) {
        model.addAttribute("project", new ProjectDto());
        if (!StringUtils.isEmpty(errorMessage)) {
            model.addAttribute("errorMessage", "*Ошибка: " + errorMessage);
        }
        return "Deploy";
    }

    @GetMapping("/projects")
    public String getProjects(Model model, @RequestParam(required = false) String errorMessage) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        if (!StringUtils.isEmpty(errorMessage)) {
            model.addAttribute("errorMessage", "*Ошибка: " + errorMessage);
        }
        return "Projects";
    }

    @GetMapping("/projects/{projectId}")
    public String getProjectById(Model model, @PathVariable Long projectId,
                                 @RequestParam(required = false) String errorMessage) {
        Project project = projectService.findProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("envs", Map.of("a", "b", "c", "1")); //TODO: set real envs

        if (!StringUtils.isEmpty(errorMessage)) {
            model.addAttribute("errorMessage", "*Ошибка: " + errorMessage);
        }
        return "Project";
    }


    @GetMapping("/")
    public String index(Model model) {
        return "Index";
    }
}
