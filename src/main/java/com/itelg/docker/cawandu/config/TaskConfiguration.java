package com.itelg.docker.cawandu.config;

import com.itelg.docker.cawandu.task.ImagePullTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class TaskConfiguration {
    @Bean
    @ConditionalOnProperty(name = "task.pullImages.pullMode", havingValue = "RUNNING")
    public ImagePullTask imagePullTask() {
        return new ImagePullTask();
    }
}