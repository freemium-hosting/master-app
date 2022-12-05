package ru.freemiumhosting.master.service;

import ru.freemiumhosting.master.exception.DeployException;
import ru.freemiumhosting.master.model.Project;

public interface DeployService {
    void deployProject(Project project) throws DeployException;
}
