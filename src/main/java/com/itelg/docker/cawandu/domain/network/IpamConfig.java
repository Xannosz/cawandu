package com.itelg.docker.cawandu.domain.network;

import lombok.Data;

@Data
public class IpamConfig {
    private String subnet;
    private String ipRange;
    private String gateway;
}
