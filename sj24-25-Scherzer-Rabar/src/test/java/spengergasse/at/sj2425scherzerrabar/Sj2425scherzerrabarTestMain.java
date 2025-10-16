package spengergasse.at.sj2425scherzerrabar;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


@Configuration
public class Sj2425scherzerrabarTestMain {

    @Value("${test.docker.db.image.name}")
    private String fullImageName;
    @Value("${test.docker.db.username}")
    private String username;
    @Value("${test.docker.db.password}")
    private String password;
    @Value("${test.docker.db.container.name}")
    private String containerName;
    @Value("${test.docker.db.name}")
    private String databaseName;
    @Value("${test.docker.db.port}")
    private Integer port;

    @Bean
    @ServiceConnection
    //@RestartScope
    PostgreSQLContainer<?> postgresContainer()  {
        final int containerPort = 5432;
        PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(port),new ExposedPort(containerPort));
        return new PostgreSQLContainer<>(DockerImageName.parse(fullImageName))
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(containerName);
                    cmd.withHostConfig(new HostConfig().withPortBindings(portBinding));
                })
                .withUsername(username)
                .withPassword(password)
                .withDatabaseName(databaseName)
                .withReuse(false);
    }

    public static void main(String[] args) {
        SpringApplication.from(Sj2425ScherzerRabarApplication::main)
                .with(Sj2425scherzerrabarTestMain.class)
                .run(args);
    }
}


