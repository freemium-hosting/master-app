package ru.freemiumhosting.master.service.impl;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.freemiumhosting.master.model.Project;
import ru.freemiumhosting.master.repository.ProjectRep;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KubernetesService {

    private final ProjectRep projectRep;

    public ApiClient createKubernetesApiClient() throws IOException, ApiException {
        //String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
        String kubeConfigPath = "src/main/resources/configs/config.yml";

        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

        return client;
    }

    public void createNamespaceIfDontExist(CoreV1Api api, Project project) {
        try {
            V1NamespaceList namespaceList = api.listNamespace(null, null, null, null, null, null, null, null, null, null);
            List<String> list =
                    namespaceList
                            .getItems()
                            .stream()
                            .map(v1Namespace -> v1Namespace.getMetadata().getName())
                            .collect(Collectors.toList());
            if (!list.contains("user1"))//TODO: CHANGE
                api.createNamespace(new V1Namespace().metadata(new V1ObjectMeta().name("user1")), null, null, null, null);
        } catch (ApiException e) {
            project.setStatus("При деплое проекта произошла ошибка");
            projectRep.save(project);
            System.out.println(e.getResponseBody());
        }
    }

    public void createService(CoreV1Api api, Project project) {
        try {
            HashMap<String, String> annotations = new HashMap<>();
            annotations.put("name", project.getKubernetesName());

            HashMap<String, String> labels = new HashMap<>();
            labels.put("app.kubernetes.io/name", project.getKubernetesName());

            ArrayList<V1ServicePort> ports = new ArrayList<>();
            ports.add(new V1ServicePort().port(8080).nodePort(project.getNodePort()));//TODO:CHANGE 8080

            api.createNamespacedService("user1", new V1Service()
                    .apiVersion("v1")
                    .metadata(new V1ObjectMeta().name(project.getKubernetesName())
                            .annotations(annotations).labels(labels))
                    .spec(new V1ServiceSpec().ports(ports).type("NodePort").selector(labels)), null, null, null, null);
        } catch (ApiException e) {
            project.setStatus("При деплое проекта произошла ошибка");
            projectRep.save(project);
            System.out.println(e.getResponseBody());
        }
    }

    public void createKubernetesObjects(Project project) {
        try {
            ApiClient client = createKubernetesApiClient();

            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();

            createNamespaceIfDontExist(api,project);
            createService(api,project);

        } catch (IOException e) {
            project.setStatus("При деплое проекта произошла ошибка");
            projectRep.save(project);
            System.out.println(e.getMessage());
        } catch (ApiException e) {
            project.setStatus("При деплое проекта произошла ошибка");
            projectRep.save(project);
            System.out.println(e.getResponseBody());
        }

    }
}
