package com.itelg.docker.cawandu.domain.container;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RestartPolicy {
    NO("no"), ON_FAILURE("on-failure"), ALWAYS("always"), UNLESS_STOPPED("unless-stopped");

    private final String name;
}
