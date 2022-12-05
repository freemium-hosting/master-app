package ru.freemiumhosting.master.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.freemiumhosting.master.exception.CleanException;
import ru.freemiumhosting.master.service.CleanerService;


@Service
@Slf4j
public class CleanerServiceImpl implements CleanerService {
    @Override
    @SneakyThrows
    public void cleanCachedLibs(String gitClonePath) {
        log.info("Старт очистки закэшированных зависимостей");
        Process process = new ProcessBuilder()
                .command("rm", "-r", "/usr/local/lib")
                .command("rm", "-r", gitClonePath)
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new CleanException("Ошибка при попытке очистить кеш");
        log.info("Очистка зависимостей завершена успешно");
    }
}
