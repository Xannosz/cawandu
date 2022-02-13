package com.itelg.docker.cawandu.config;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.RegistryAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Configuration
public class DockerConfiguration {
    private String dockerRegistryUsername;
    private String dockerRegistryEmail;
    private String dockerRegistryPassword;
    private String dockerHostUri;
    private String dockerHostCertificatesDirectory;

    @Autowired
    private Environment env;

    @PostConstruct
    public void postConstruct() {
        // Required to fix a bug in docker-maven-plugin
        dockerRegistryUsername = env.getProperty("docker.registry.username").replace("null", "");
        dockerRegistryEmail = env.getProperty("docker.registry.email").replace("null", "");
        dockerRegistryPassword = env.getProperty("docker.registry.password").replace("null", "");
        dockerHostUri = env.getProperty("docker.host.uri").replace("null", "");
        dockerHostCertificatesDirectory = env.getProperty("docker.host.certificates.directory").replace("null", "");
    }

    @Bean
    public DockerClient dockerClient() throws DockerCertificateException {
        DefaultDockerClient.Builder dockerClientBuilder = getDockerClientBuilder();
        RegistryAuth authConfig = getAuthConfig();
        if (Objects.nonNull(authConfig)) {
            dockerClientBuilder.registryAuth(getAuthConfig());
        }
        DockerClient dockerClient = dockerClientBuilder.build();
        log.info("Docker client:" + dockerClient);
        return dockerClient;
    }

    private DefaultDockerClient.Builder getDockerClientBuilder() throws DockerCertificateException {
        if (isNotBlank(dockerHostUri) && isNotBlank(dockerHostCertificatesDirectory)) {
            DefaultDockerClient.Builder dockerClientBuilder = DefaultDockerClient.builder();
            dockerClientBuilder.uri(dockerHostUri);
            dockerClientBuilder.dockerCertificates(new DockerCertificates(Paths.get(dockerHostCertificatesDirectory.trim())));
            return dockerClientBuilder;
        } else if (Files.exists(Paths.get("/var/run/docker.sock"))) {
            return DefaultDockerClient.fromEnv();
        } else {
            throw new RuntimeException("Invalid host-configuration! Either mount \"docker.sock\" or provide \"HOST_URI\" and \"HOST_CERTIFICATES\"!");
        }
    }

    private RegistryAuth getAuthConfig() {
        if (isNotBlank(dockerRegistryUsername) && isNotBlank(dockerRegistryEmail) && isNotBlank(dockerRegistryPassword)) {
            RegistryAuth.Builder authConfigBuilder = RegistryAuth.builder();
            authConfigBuilder.username(dockerRegistryUsername);
            authConfigBuilder.email(dockerRegistryEmail);
            authConfigBuilder.password(dockerRegistryPassword);
            return authConfigBuilder.build();
        }

        return null;
    }

    @Bean
    public String dockerHost(DockerClient dockerClient) throws InterruptedException, DockerException {
        return dockerClient.info().name();
    }
}