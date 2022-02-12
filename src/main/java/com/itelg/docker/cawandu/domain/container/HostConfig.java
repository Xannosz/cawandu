package com.itelg.docker.cawandu.domain.container;

import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class HostConfig {
    @Singular
    private Map<Integer, Integer> portBindings;
    @Singular
    private Set<ContainerMount> volumes;
    private List<String> links;
    private boolean publishAllPorts;
    private String networkMode;
    private RestartPolicy restartPolicy;
    private int retryNumber;
    private boolean autoRemove;
}
