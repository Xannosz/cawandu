package com.itelg.docker.cawandu.repository.dockerclient;

import com.itelg.docker.cawandu.domain.volume.Volume;
import com.itelg.docker.cawandu.domain.volume.VolumeFilter;
import com.itelg.docker.cawandu.domain.volume.VolumeList;
import com.itelg.docker.cawandu.repository.VolumeRepository;
import com.itelg.docker.cawandu.repository.dockerclient.converter.DockerConverter;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DockerVolumeRepository implements VolumeRepository {
    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private DockerConverter dockerConverter;

    @Override
    public Volume createVolume() {
        try {
            return dockerConverter.convert(dockerClient.createVolume());
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to create volume.", e);
        }
        return null;
    }

    @Override
    public Volume createVolume(Volume volume) {
        try {
            return dockerConverter.convert(dockerClient.createVolume(dockerConverter.convert(volume)));
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to create volume.", e);
        }
        return null;
    }

    @Override
    public Volume inspectVolume(String id) {
        try {
            return dockerConverter.convert(dockerClient.inspectVolume(id));
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to inspect volume.", e);
        }
        return null;
    }

    @Override
    public void removeVolume(Volume volume) {
        try {
            dockerClient.removeVolume(dockerConverter.convert(volume));
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to remove volume.", e);
        }
    }

    @Override
    public void removeVolume(String id) {
        try {
            dockerClient.removeVolume(id);
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to remove volume.", e);
        }
    }

    @Override
    public VolumeList listVolumes(VolumeFilter volumeFilter) {
        try {
            VolumeList volumeList = dockerConverter.convert(dockerClient.listVolumes());
            VolumeList filteredVolumes = new VolumeList(volumeList);

            for (Volume volume : volumeList.getVolumes()) {
                if (StringUtils.isNotBlank(volumeFilter.getName())) {
                    if (!volume.getName().contains(volumeFilter.getName())) {
                        filteredVolumes.getVolumes().remove(volume);
                    }
                }
            }

            return filteredVolumes;
        } catch (DockerException | InterruptedException e) {
            log.error("Failed to remove volume.", e);
        }
        return null;
    }
}
