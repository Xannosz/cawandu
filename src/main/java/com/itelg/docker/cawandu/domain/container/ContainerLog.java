package com.itelg.docker.cawandu.domain.container;

import lombok.Data;

import java.util.Date;

@Data
public class ContainerLog {
    private String log;
    private String stream;
    private Date time;
}
