package com.itelg.docker.cawandu.service.impl;

import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.docker.cawandu.domain.volume.VolumeFilter;
import com.itelg.docker.cawandu.domain.volume.VolumeList;
import com.itelg.docker.cawandu.repository.VolumeRepository;
import com.itelg.docker.cawandu.service.VolumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultVolumeService implements VolumeService {

    @Autowired
    private VolumeRepository volumeRepository;

    @Override
    public Volume createVolume(Volume volume) {
        return volumeRepository.createVolume(volume);
    }

    @Override
    public Volume inspectVolume(String id) {
        return volumeRepository.inspectVolume(id);
    }

    @Override
    public void removeVolume(String id) {
        volumeRepository.removeVolume(id);
    }

    @Override
    public VolumeList listVolumes(VolumeFilter volumeFilter) {
        return volumeRepository.listVolumes(volumeFilter);
    }
}
