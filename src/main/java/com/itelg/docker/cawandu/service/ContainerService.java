package com.itelg.docker.cawandu.service;

import com.itelg.docker.cawandu.domain.container.*;

import java.util.List;
import java.util.Map;

public interface ContainerService {
    void renameContainer(Container container, String newName);

    void recreateContainer(Container container);

    void switchTag(Container container, String tag);

    void startContainer(Container container);

    void stopContainer(Container container);

    void restartContainer(Container container);

    void removeContainer(Container container);

    void killContainer(Container container);

    Container getContainerById(String id);

    Container getContainerByName(String name);

    List<Container> getContainersByFilter(ContainerFilter filter);

    List<Container> getAllContainers();

    List<ContainerLog> getContainerLog(Container container);

    Map<ContainerState, Integer> getContainerStateStats();

    ContainerCreation createContainer(ContainerConfig containerConfig);
}