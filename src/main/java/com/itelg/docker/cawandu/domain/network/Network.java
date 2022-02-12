package com.itelg.docker.cawandu.domain.network;

import lombok.Data;

import java.util.Map;

@Data
public class Network {
    private String id;
    private String name;
    private String scope;
    private String driver;
    private Ipam ipam;
    private Map<String, Container> containers;
    private Map<String, String> options;

    @Data
    public static class Container {
        private String endpointId;
        private String macAddress;
        private String ipv4Address;
        private String ipv6Address;
    }
}
