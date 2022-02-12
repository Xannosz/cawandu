package com.itelg.docker.cawandu.domain.container;

import lombok.Data;

@Data
public class ContainerMount {
    private String source;
    private String destination;
    private String mode; //TODO not in use
    private boolean rw;
}
