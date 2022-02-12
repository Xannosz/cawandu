package com.itelg.docker.cawandu.domain.network;

import com.itelg.docker.cawandu.domain.filter.AbstractFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NetworkFilter extends AbstractFilter {
    private String id;
    private String name;
}
