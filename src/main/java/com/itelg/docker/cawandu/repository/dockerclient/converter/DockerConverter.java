package com.itelg.docker.cawandu.repository.dockerclient.converter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.itelg.docker.cawandu.domain.container.ContainerConfig;
import com.itelg.docker.cawandu.domain.network.IpamConfig;
import com.itelg.docker.cawandu.domain.network.NetworkConfig;
import com.spotify.docker.client.messages.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DockerConverter {
    public Volume convert(com.itelg.docker.cawandu.domain.volume.Volume volume) {
        return Volume.builder().name(volume.getName())
                .driver(volume.getDriver())
                .driverOpts(volume.getDriverOpts())
                .labels(volume.getLabels())
                .mountpoint(volume.getMountPoint())
                .scope(volume.getScope())
                .status(volume.getStatus()).build();
    }

    public com.itelg.docker.cawandu.domain.volume.Volume convert(Volume volume) {
        com.itelg.docker.cawandu.domain.volume.Volume result = new com.itelg.docker.cawandu.domain.volume.Volume();
        result.setName(volume.name());
        result.setDriver(volume.driver());
        result.setDriverOpts(volume.driverOpts());
        result.setLabels(volume.labels());
        result.setMountPoint(volume.mountpoint());
        result.setScope(volume.scope());
        result.setStatus(volume.status());
        return result;
    }

    public com.itelg.docker.cawandu.domain.volume.VolumeList convert(VolumeList volumeList) {
        com.itelg.docker.cawandu.domain.volume.VolumeList result = new com.itelg.docker.cawandu.domain.volume.VolumeList();
        result.setVolumes(volumeList.volumes().stream().map(this::convert).collect(Collectors.toList()));
        result.setWarnings(volumeList.warnings());
        return result;
    }

    public com.itelg.docker.cawandu.domain.network.Network convert(Network network) {
        com.itelg.docker.cawandu.domain.network.Network result = new com.itelg.docker.cawandu.domain.network.Network();
        result.setId(network.id());
        result.setName(network.name());
        result.setScope(network.scope());
        result.setDriver(network.driver());
        result.setIpam(convert(network.ipam()));
        result.setContainers(convertContainer(network.containers()));
        result.setOptions(network.options());
        return result;
    }

    public com.itelg.docker.cawandu.domain.network.Ipam convert(Ipam ipam) {
        com.itelg.docker.cawandu.domain.network.Ipam result = new com.itelg.docker.cawandu.domain.network.Ipam();
        result.setDriver(ipam.driver());
        result.setConfig(ipam.config().stream().map(c -> {
            IpamConfig config = new IpamConfig();
            config.setSubnet(c.subnet());
            config.setGateway(c.gateway());
            config.setIpRange(c.ipRange());
            return config;
        }).collect(Collectors.toList()));
        return result;
    }

    public Ipam convert(com.itelg.docker.cawandu.domain.network.Ipam ipam) {
        return Ipam.builder().driver(ipam.getDriver())
                .config(ipam.getConfig().stream().map(c ->
                        com.spotify.docker.client.messages.IpamConfig.create(c.getSubnet(), c.getGateway(), c.getIpRange())
                ).collect(Collectors.toList())).build();
    }

    public com.spotify.docker.client.messages.NetworkConfig convert(NetworkConfig networkConfig) {
        return com.spotify.docker.client.messages.NetworkConfig
                .builder().name(networkConfig.getName())
                .driver(networkConfig.getDriver())
                .ipam(convert(networkConfig.getIpam()))
                .options(networkConfig.getOptions())
                .checkDuplicate(networkConfig.getCheckDuplicate()).build();
    }

    public com.itelg.docker.cawandu.domain.network.NetworkCreation convert(NetworkCreation networkCreation) {
        com.itelg.docker.cawandu.domain.network.NetworkCreation result = new com.itelg.docker.cawandu.domain.network.NetworkCreation();
        result.setId(networkCreation.id());
        result.setWarnings(networkCreation.warnings());
        return result;
    }

    private Map<String, com.itelg.docker.cawandu.domain.network.Network.Container> convertContainer(ImmutableMap<String, Network.Container> containers) {
        if (Objects.isNull(containers)) {
            return null;
        }
        return containers.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, c -> {
                    com.itelg.docker.cawandu.domain.network.Network.Container container = new com.itelg.docker.cawandu.domain.network.Network.Container();
                    container.setEndpointId(c.getValue().endpointId());
                    container.setIpv4Address(c.getValue().ipv4Address());
                    container.setIpv6Address(c.getValue().ipv6Address());
                    container.setMacAddress(c.getValue().macAddress());
                    return container;
                }));
    }

    public com.spotify.docker.client.messages.ContainerConfig convert(ContainerConfig containerConfig) {
        Set<String> extPorts = new HashSet<>();
        Map<String, List<PortBinding>> portBindings = new HashMap<>();

        for (Map.Entry<Integer, Integer> portPair : containerConfig.getHostConfig().getPortBindings().entrySet()) {
            List<PortBinding> hostPorts = new ArrayList<>();
            hostPorts.add(PortBinding.of("0.0.0.0", portPair.getKey()));
            portBindings.put(portPair.getValue().toString(), hostPorts);
            extPorts.add(portPair.getValue().toString());
        }

        final HostConfig.Builder hostConfigBuilder = HostConfig.builder();

        for (com.itelg.docker.cawandu.domain.container.ContainerMount containerMount : containerConfig.getHostConfig().getVolumes()) {
            if (!Strings.isNullOrEmpty(containerMount.getSource()) && !Strings.isNullOrEmpty(containerMount.getDestination())) {
                hostConfigBuilder.appendBinds(containerMount.getSource() + ":" + containerMount.getDestination()
                        + (containerMount.isRw() ? "rw" : "ro"));
            }
        }
        hostConfigBuilder.portBindings(portBindings);
        hostConfigBuilder.links(containerConfig.getHostConfig().getLinks());
        hostConfigBuilder.publishAllPorts(containerConfig.getHostConfig().isPublishAllPorts());
        hostConfigBuilder.networkMode(containerConfig.getHostConfig().getNetworkMode());
        hostConfigBuilder.autoRemove(containerConfig.getHostConfig().isAutoRemove());

        switch (containerConfig.getHostConfig().getRestartPolicy()) {
            case NO:
                break;
            case ON_FAILURE:
                hostConfigBuilder.restartPolicy(HostConfig.RestartPolicy.onFailure(containerConfig.getHostConfig().getRetryNumber()));
                break;
            case ALWAYS:
                hostConfigBuilder.restartPolicy(HostConfig.RestartPolicy.always());
                break;
            case UNLESS_STOPPED:
                hostConfigBuilder.restartPolicy(HostConfig.RestartPolicy.unlessStopped());
                break;
        }

        List<String> env = new ArrayList<>();
        for (Map.Entry<String, String> envEntry : containerConfig.getEnv().entrySet()) {
            env.add(envEntry.getKey() + ":" + envEntry.getValue());
        }

        return com.spotify.docker.client.messages.ContainerConfig.builder()
                .hostConfig(hostConfigBuilder.build()).exposedPorts(extPorts)
                .workingDir(containerConfig.getWorkingDir()).user(containerConfig.getUser())
                .cmd(containerConfig.getCmd()).env(env)
                .image(containerConfig.getImage() + ":" + containerConfig.getTag()).build();
    }

    public com.itelg.docker.cawandu.domain.container.ContainerCreation convert(ContainerCreation containerCreation) {
        com.itelg.docker.cawandu.domain.container.ContainerCreation result = new com.itelg.docker.cawandu.domain.container.ContainerCreation();
        result.setId(containerCreation.id());
        result.setWarnings(containerCreation.warnings());
        return result;
    }
}
