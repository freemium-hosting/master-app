package ru.freemiumhosting.master.model;

public enum ProjectStatus {
    UNDEFINED, //Проект не создан
    CREATED, //Скачаны сорцы с гита, создан докерфайл
    RUNNING, //Работает в кластере
    ERROR, //В ошибке
    STOPPED, //Остановлен но может быть запущен
    DELETED //Удален
}
