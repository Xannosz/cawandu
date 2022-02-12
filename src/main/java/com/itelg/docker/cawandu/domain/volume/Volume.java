package com.itelg.docker.cawandu.domain.volume;

import lombok.Data;

import java.util.Map;

@Data
public class Volume {
    private String name;
    private String driver;
    private Map<String, String> driverOpts;
    private Map<String, String> labels;
    private String mountPoint;
    private String scope;
    private Map<String, String> status;
}
