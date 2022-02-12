package com.itelg.docker.cawandu.service;

import com.itelg.docker.cawandu.domain.network.Network;
import com.itelg.docker.cawandu.domain.network.NetworkConfig;
import com.itelg.docker.cawandu.domain.network.NetworkCreation;
import com.itelg.docker.cawandu.domain.network.NetworkFilter;

import java.util.List;

public interface NetworkService {
    List<Network> listNetworks(NetworkFilter networkFilter);

    Network inspectNetwork(String id);

    NetworkCreation createNetwork(NetworkConfig networkConfig);

    void removeNetwork(String id);

    void connectToNetwork(String var1, String var2);

    void disconnectFromNetwork(String var1, String var2);
}
