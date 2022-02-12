package com.itelg.docker.cawandu.domain.network;

import lombok.Data;

import java.util.Map;

@Data
public class NetworkConfig {
    private String name;
    private String driver;
    private Ipam ipam;
    private Map<String, String> options;
    private Boolean checkDuplicate;
}
