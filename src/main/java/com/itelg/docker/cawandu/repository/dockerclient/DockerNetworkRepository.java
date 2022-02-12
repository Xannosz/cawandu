package com.itelg.docker.cawandu.repository.dockerclient;

import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.domain.network.NetworkConfig;
import com.itelg.docker.cawandu.domain.network.NetworkCreation;
import com.itelg.docker.cawandu.domain.network.NetworkFilter;
import com.itelg.docker.cawandu.repository.NetworkRepository;
import com.itelg.docker.cawandu.repository.dockerclient.converter.DockerConverter;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DockerNetworkRepository implements NetworkRepository {
    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private DockerConverter dockerConverter;

    @Override
    public List<Network> listNetworks(NetworkFilter networkFilter) {
        try {
            List<Network> networks = dockerClient.listNetworks().stream().map(n -> dockerConverter.convert(n)).collect(Collectors.toList());
            List<Network> filteredNetworks = new ArrayList<>(networks);

            for (Network network : networks) {
                if (StringUtils.isNotBlank(networkFilter.getId())) {
                    if (!network.getId().contains(networkFilter.getId())) {
                        filteredNetworks.remove(network);
                    }
                }
                if (StringUtils.isNotBlank(networkFilter.getName())) {
                    if (!network.getName().contains(networkFilter.getName())) {
                        filteredNetworks.remove(network);
                    }
                }
            }

            return filteredNetworks;
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to list networks.", e);
        }
        return null;
    }

    @Override
    public Network inspectNetwork(String id) {
        try {
            return dockerConverter.convert(dockerClient.inspectNetwork(id));
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to inspect network.", e);
        }
        return null;
    }

    @Override
    public NetworkCreation createNetwork(NetworkConfig networkConfig) {
        try {
            return dockerConverter.convert(dockerClient.createNetwork(dockerConverter.convert(networkConfig)));
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to create network.", e);
        }
        return null;
    }

    @Override
    public void removeNetwork(String id) {
        try {
            dockerClient.removeNetwork(id);
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to remove network.", e);
        }
    }

    @Override
    public void connectToNetwork(String var1, String var2) {
        try {
            dockerClient.connectToNetwork(var1, var2);
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to connect network.", e);
        }
    }

    @Override
    public void disconnectFromNetwork(String var1, String var2) {
        try {
            dockerClient.disconnectFromNetwork(var1, var2);
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to disconnect network.", e);
        }
    }
}
