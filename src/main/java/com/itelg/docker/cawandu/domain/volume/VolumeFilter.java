package com.itelg.docker.cawandu.domain.volume;

import com.itelg.docker.cawandu.domain.filter.AbstractFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VolumeFilter extends AbstractFilter {
    private String name;
}
