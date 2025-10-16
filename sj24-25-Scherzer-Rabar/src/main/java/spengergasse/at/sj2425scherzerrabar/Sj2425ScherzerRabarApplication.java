package spengergasse.at.sj2425scherzerrabar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Sj2425ScherzerRabarApplication {

    public static void main(String[] args) {
        SpringApplication.run(Sj2425ScherzerRabarApplication.class, args);
    }

}
