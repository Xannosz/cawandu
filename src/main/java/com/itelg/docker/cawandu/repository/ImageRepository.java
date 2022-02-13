package com.itelg.docker.cawandu.repository;

import com.itelg.docker.cawandu.domain.image.Image;
import com.itelg.docker.cawandu.domain.image.ImageFilter;
import com.itelg.docker.cawandu.domain.image.UpdateState;

import java.util.List;

public interface ImageRepository {
    UpdateState pullImage(Image image);

    UpdateState pullImage(String imageName);

    boolean removeImage(Image image);

    List<Image> getImagesByFilter(ImageFilter filter);

    List<Image> getAllImages();
}