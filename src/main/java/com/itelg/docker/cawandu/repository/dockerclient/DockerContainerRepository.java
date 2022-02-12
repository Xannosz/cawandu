package com.itelg.docker.cawandu.repository.dockerclient;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.itelg.docker.cawandu.domain.container.*;
import com.itelg.docker.cawandu.repository.ContainerRepository;
import com.itelg.docker.cawandu.repository.dockerclient.converter.ContainerConverter;
import com.itelg.docker.cawandu.repository.dockerclient.converter.DockerConverter;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;
import hu.xannosz.microtools.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DockerContainerRepository implements ContainerRepository {
    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ContainerConverter containerConverter;

    @Autowired
    private DockerConverter dockerConverter;

    @Override
    public void renameContainer(Container container, String newName) {
        try {
            dockerClient.renameContainer(container.getId(), newName);
            container.setName(newName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void recreateContainer(Container container) {
        ContainerInfo containerInfo = getContainerInfoById(container.getId());
        ContainerConfig.Builder containerConfigBuilder = ContainerConfig.builder();
        containerConfigBuilder.hostConfig(containerInfo.hostConfig());
        containerConfigBuilder.env(containerInfo.config().env());
        containerConfigBuilder.labels(container.getLabels());
        containerConfigBuilder.volumes(containerInfo.config().volumes());
        containerConfigBuilder.image(container.getImageName());
        containerConfigBuilder.exposedPorts(containerInfo.config().exposedPorts());
        containerConfigBuilder.user(containerInfo.config().user());
        containerConfigBuilder.workingDir(containerInfo.config().workingDir());

        stopContainer(container);
        removeContainer(container);
        String containerId = createContainer(containerConfigBuilder.build(), container.getName());

        if (container.getState() == ContainerState.UP) {
            Container newContainer = getContainerById(containerId);
            startContainer(newContainer);
        }
    }

    private ContainerInfo getContainerInfoById(String id) {
        try {
            return dockerClient.inspectContainer(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    private String createContainer(ContainerConfig containerConfig, String name) {
        try {
            return dockerClient.createContainer(containerConfig, name).id();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void startContainer(Container container) {
        try {
            dockerClient.startContainer(container.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void stopContainer(Container container) {
        try {
            dockerClient.stopContainer(container.getId(), 5);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void restartContainer(Container container) {
        try {
            dockerClient.restartContainer(container.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void removeContainer(Container container) {
        try {
            dockerClient.removeContainer(container.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void killContainer(Container container) {
        try {
            dockerClient.killContainer(container.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Container getContainerById(String id) {
        ContainerFilter filter = new ContainerFilter();
        filter.setId(id);
        List<Container> containers = getContainersByFilter(filter);

        return (containers.isEmpty() ? null : containers.get(0));
    }

    @Override
    public Container getContainerByName(String name) {
        ContainerFilter filter = new ContainerFilter();
        filter.setName(name);
        List<Container> containers = getContainersByFilter(filter);

        return (containers.isEmpty() ? null : containers.get(0));
    }

    @Override
    public List<Container> getContainersByFilter(ContainerFilter filter) {
        List<Container> allContainers = getAllContainers();
        List<Container> filteredContainers = new ArrayList<>(allContainers);

        for (Container container : allContainers) {
            if (StringUtils.isNotBlank(filter.getId())) {
                if (!container.getId().contains(filter.getId())) {
                    filteredContainers.remove(container);
                }
            }

            if (StringUtils.isNotBlank(filter.getName())) {
                if (!container.getName().contains(filter.getName())) {
                    filteredContainers.remove(container);
                }
            }

            if (filter.getState() != null) {
                if (container.getState() != filter.getState()) {
                    filteredContainers.remove(container);
                }
            }

            if (StringUtils.isNotBlank(filter.getImageName())) {
                if (container.getImageName() == null || !container.getImageName().contains(filter.getImageName())) {
                    filteredContainers.remove(container);
                }
            }
        }

        return filteredContainers;
    }

    @Override
    public List<Container> getAllContainers() {
        try {
            return containerConverter.convert(dockerClient.listContainers(ListContainersParam.allContainers()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<ContainerLog> getContainerLog(Container container) {
        ContainerInfo containerInfo = getContainerInfoById(container.getId());
        try {
            List<String> lines = Files.readLines(new File(containerInfo.logPath()), Charsets.UTF_8);
            return lines.stream().map(l -> Json.readData(l, ContainerLog.class)).collect(Collectors.toList());
        } catch (Exception | Error e) {
            log.error("Failed to get logs from container {}:", container.getName(), e);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Map<ContainerState, Integer> getContainerStateStats() {
        Map<ContainerState, Integer> stats = new LinkedHashMap<>();

        for (ContainerState state : ContainerState.values()) {
            stats.put(state, 0);
        }

        for (Container container : getAllContainers()) {
            int count = (stats.get(container.getState()) + 1);
            stats.put(container.getState(), count);
        }

        return stats;
    }

    @Override
    public ContainerCreation createContainer(com.itelg.docker.cawandu.domain.container.ContainerConfig containerConfig) {
        try {
            return dockerConverter.convert(dockerClient.createContainer(dockerConverter.convert(containerConfig), containerConfig.getName()));
        } catch (Exception e) {
            log.error("Container creation failed.", e);
        }
        return null;
    }
}