package ru.freemiumhosting.master.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import ru.freemiumhosting.master.exception.GitCloneException;

@Slf4j
@Service
public class GitService {
    public @NonNull String cloneGitRepo(String gitClonePath, String uri, String branch)
        throws GitCloneException {
        try (Git git = Git.cloneRepository()
            .setURI(uri)
            .setDirectory(new File(gitClonePath))
            .setBranch(branch)
            .call()
        ) {
            return git.getRepository().findRef(branch).getObjectId().getName();
        } catch (GitAPIException | IOException ex) {
            log.error("Возникла проблема при клонировании git репозитория", ex);
            throw new GitCloneException("Возникла проблема при клонировании git репозитория: " + ex.getMessage());
        }
    }
}
