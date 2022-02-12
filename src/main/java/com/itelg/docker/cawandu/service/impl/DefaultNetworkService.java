package com.itelg.docker.cawandu.service.impl;

import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.domain.network.NetworkConfig;
import com.itelg.docker.cawandu.domain.network.NetworkCreation;
import com.itelg.docker.cawandu.domain.network.NetworkFilter;
import com.itelg.docker.cawandu.repository.NetworkRepository;
import com.itelg.docker.cawandu.service.NetworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DefaultNetworkService implements NetworkService {

    @Autowired
    private NetworkRepository networkRepository;

    @Override
    public List<Network> listNetworks(NetworkFilter networkFilter) {
        return networkRepository.listNetworks(networkFilter);
    }

    @Override
    public Network inspectNetwork(String id) {
        return networkRepository.inspectNetwork(id);
    }

    @Override
    public NetworkCreation createNetwork(NetworkConfig networkConfig) {
        return networkRepository.createNetwork(networkConfig);
    }

    @Override
    public void removeNetwork(String id) {
        networkRepository.removeNetwork(id);
    }

    @Override
    public void connectToNetwork(String var1, String var2) {
        networkRepository.connectToNetwork(var1, var2);
    }

    @Override
    public void disconnectFromNetwork(String var1, String var2) {
        networkRepository.disconnectFromNetwork(var1, var2);
    }
}
