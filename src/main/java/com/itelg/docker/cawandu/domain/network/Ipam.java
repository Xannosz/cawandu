package com.itelg.docker.cawandu.domain.network;

import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
public class Ipam {
    private String driver;
    @Singular
    private List<IpamConfig> config;
}
