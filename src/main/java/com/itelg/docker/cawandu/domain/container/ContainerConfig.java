package com.itelg.docker.cawandu.domain.container;

import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Data
public class ContainerConfig {
    private String image;
    private String tag;
    private String name;
    private String user;
    @Singular
    private Map<String, String> env;
    @Singular
    private List<String> cmd;
    private String workingDir;
    private HostConfig hostConfig;
}
