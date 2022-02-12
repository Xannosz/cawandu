package com.itelg.docker.cawandu.domain.container;

import lombok.Data;

import java.util.List;

@Data
public class ContainerCreation {
    private String id;
    private List<String> warnings;
}
