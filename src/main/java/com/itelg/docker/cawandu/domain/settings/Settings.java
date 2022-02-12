package com.itelg.docker.cawandu.domain.settings;

import com.itelg.docker.cawandu.domain.container.ContainerConfig;
import com.itelg.docker.cawandu.domain.network.NetworkConfig;
import com.itelg.docker.cawandu.domain.volume.Volume;
import lombok.Data;

import java.util.*;

@Data
public class Settings {
    private Set<ContainerConfig> dockerConfigurations = new HashSet<>();
    private Set<NetworkConfig> networkConfigurations = new HashSet<>();
    private Set<Volume> volumeConfigurations = new HashSet<>();
    private Date nextRestartDate = new Date();
    private long timeToRestart = 1000 * 60 * 5;
    private String selfName = "cawandu";
    private Map<String, String> userPassword = new HashMap<>();
    private Set<String> enabledUsers = new HashSet<>();
    private String mainPage;
}
