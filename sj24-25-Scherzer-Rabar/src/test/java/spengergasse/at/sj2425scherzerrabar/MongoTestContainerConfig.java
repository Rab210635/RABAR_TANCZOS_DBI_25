package spengergasse.at.sj2425scherzerrabar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class MongoTestContainerConfig {

    @Value("${test.docker.mongo.image.name}")
    private String mongoImage;

    @Value("${test.docker.mongo.container.name}")
    private String containerName;

    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {
        return new MongoDBContainer(DockerImageName.parse(mongoImage))
                .withReuse(false);
    }
}
