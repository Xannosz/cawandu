package com.itelg.docker.cawandu.domain.volume;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class VolumeList {
    private List<Volume> volumes = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();

    public VolumeList(VolumeList volumeList) {
        if (Objects.nonNull(volumeList)) {
            if (Objects.nonNull(volumeList.getVolumes())) {
                volumes = new ArrayList<>(volumeList.getVolumes());
            }
            if (Objects.nonNull(volumeList.getWarnings())) {
                warnings = new ArrayList<>(volumeList.getWarnings());
            }
        }
    }
}