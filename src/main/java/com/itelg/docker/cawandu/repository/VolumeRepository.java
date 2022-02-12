package com.itelg.docker.cawandu.repository;

import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.docker.cawandu.domain.volume.VolumeFilter;
import com.itelg.docker.cawandu.domain.volume.VolumeList;

public interface VolumeRepository {
    Volume createVolume();

    Volume createVolume(Volume volume);

    Volume inspectVolume(String id);

    void removeVolume(Volume volume);

    void removeVolume(String id);

    VolumeList listVolumes(VolumeFilter volumeFilter);
}
