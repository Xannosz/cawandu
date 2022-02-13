package com.itelg.docker.cawandu.repository.dockerclient.converter;

import com.itelg.docker.cawandu.domain.container.ContainerState;
import com.spotify.docker.client.messages.Container;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ContainerConverter implements Converter<Container, com.itelg.docker.cawandu.domain.container.Container> {
    @Override
    public com.itelg.docker.cawandu.domain.container.Container convert(Container clientContainer) {
        com.itelg.docker.cawandu.domain.container.Container container = new com.itelg.docker.cawandu.domain.container.Container();
        container.setId(clientContainer.id());
        container.setState(ContainerState.fromString(clientContainer.status()));
        container.setName(clientContainer.names().get(0).substring(1));
        container.setImageName(!clientContainer.image().equals(clientContainer.imageId()) ? clientContainer.image() : null);
        container.setImageId(clientContainer.imageId());
        container.setUpdate(clientContainer.image().equals(clientContainer.imageId()));
        container.setLabels(new HashMap<>(clientContainer.labels()));
        container.setCreated(LocalDateTime.ofEpochSecond(clientContainer.created(), 0, ZoneOffset.UTC));
        return container;
    }

    public List<com.itelg.docker.cawandu.domain.container.Container> convert(List<Container> clientContainers) {
        List<com.itelg.docker.cawandu.domain.container.Container> containers = new ArrayList<>();

        for (Container clientContainer : clientContainers) {
            containers.add(convert(clientContainer));
        }

        return containers;
    }
}