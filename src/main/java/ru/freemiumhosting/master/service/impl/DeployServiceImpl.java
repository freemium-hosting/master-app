package ru.freemiumhosting.master.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.freemiumhosting.master.exception.DeployException;
import ru.freemiumhosting.master.model.Project;
import ru.freemiumhosting.master.model.ProjectStatus;
import ru.freemiumhosting.master.repository.ProjectRep;
import ru.freemiumhosting.master.service.CleanerService;
import ru.freemiumhosting.master.service.DeployService;

import java.nio.file.Path;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeployServiceImpl implements DeployService {
    private final DockerImageBuilderService dockerImageBuilderService;
    private final CleanerService cleanerService;
    private final ProjectRep projectRep;
    private final KubernetesService kubernetesService;
    @Value("${freemium.hosting.git-clone-path}")
    String clonePath;

    @Override
    @Async
    public void deployProject(Project project) throws DeployException {
        try {
            log.info(String.format("Старт деплоя проекта %s", project.getName()));
            dockerImageBuilderService.pushImageToRegistry(project);
            cleanerService.cleanCachedLibs(Path.of(clonePath, project.getName()).toString());
            projectRep.save(project);//сначала сохраняем, чтобы id сгенерировалось
            project.setKubernetesName("project" + project.getId());
            generateProjectNodePort(project);
            kubernetesService.createKubernetesObjects(project);
            //TODO: clear dockerhub repository
            project.setStatus(ProjectStatus.RUNNING);
            projectRep.save(project);
            log.info(String.format("Проект %s успешно задеплоен", project.getName()));
        } catch (DeployException exception) {
            project.setStatus(ProjectStatus.ERROR);
            log.error(String.format("Ошибка при деплое проекта %s", project.getName()), exception);
        }
    }

    public void generateProjectNodePort(Project project) {
        Random random = new Random();
        while (project.getNodePort() == null) {
            Integer nodePort = 30000 + random.nextInt(2767);
            if (!projectRep.existsByNodePort(nodePort))
                project.setNodePort(nodePort);
        }
    }
}
