package com.itelg.docker.cawandu.service;

import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.docker.cawandu.domain.volume.VolumeFilter;
import com.itelg.docker.cawandu.domain.volume.VolumeList;

public interface VolumeService {
    Volume createVolume(Volume volume);

    Volume inspectVolume(String id);

    void removeVolume(String id);

    VolumeList listVolumes(VolumeFilter volumeFilter);
}
