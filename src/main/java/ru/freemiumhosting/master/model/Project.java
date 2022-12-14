package ru.freemiumhosting.master.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.AbstractMap;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projects_generator")
    @SequenceGenerator(name = "projects_generator", sequenceName = "projects_seq", allocationSize = 500)
    private Long id;
    private String name;
    private String link;
    private String branch;
    private ProjectStatus status = ProjectStatus.UNDEFINED;//TODO change to enum
    private String language;
    private String executableName;
    private String registryDestination;
    private String lastLaunch = "true";
    private String currentLaunch = "true";
    private String kubernetesName;
    private Integer nodePort;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Boolean userStartsDeploy() {
        return lastLaunch.equals("false") && currentLaunch.equals("true");
    }

    public Boolean userFinishesDeploy() {
        return lastLaunch.equals("true") && currentLaunch.equals("false");
    }

}
