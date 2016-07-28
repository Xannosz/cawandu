package com.itelg.docker.cawandu.domain.container;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class Container
{
    public static final String CONTAINER_NAME_PATTERN = "/?[a-zA-Z0-9_-]+";
    public static final String IMAGE_NAME_LABEL = "cawandu.image.name";

    private String id;
    private ContainerState state;
    private String name;
    private String imageName;
    private String imageId;
    private Map<String, String> labels;
    private LocalDateTime created;

    public String getImageName()
    {
        if (hasLabel(IMAGE_NAME_LABEL))
        {
            return getLabel(IMAGE_NAME_LABEL);
        }

        return imageName;
    }

    public String getImageTag()
    {
        if (StringUtils.isNotBlank(getImageName()))
        {
            if (getImageName().contains(":"))
            {
                return getImageName().split(":")[1];
            }

            return "latest";
        }

        return null;
    }

    public String getImageNameWithoutTag()
    {
        if (StringUtils.isNotBlank(getImageName()))
        {
            return getImageName().split(":")[0];
        }

        return null;
    }

    public boolean hasLabel(String key)
    {
        if (labels != null)
        {
            return labels.containsKey(key);
        }

        return false;
    }

    public String getLabel(String key)
    {
        if (labels != null)
        {
            return labels.get(key);
        }

        return null;
    }

    public void addLabel(String key, String value)
    {
        if (labels == null)
        {
            labels = new HashMap<>();
        }

        labels.put(key, value);
    }

    public boolean isStartable()
    {
        return (state == ContainerState.CREATED || state == ContainerState.EXITED);
    }

    public boolean isStoppable()
    {
        return (state == ContainerState.UP || state == ContainerState.RESTARTING);
    }

    public boolean isRestartable()
    {
        return (state == ContainerState.UP);
    }

    public boolean isRemovable()
    {
        return (state == ContainerState.CREATED || state == ContainerState.EXITED);
    }

    public boolean isKillable()
    {
        return true;
    }

    public boolean hasUpdate()
    {
        return (imageId.equals(imageName) && !getImageName().contains("sha256"));
    }

    public boolean isTagSwitchable()
    {
        return !getImageName().contains("sha256");
    }
}