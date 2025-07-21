package de.toxic2302.inventarbuddy;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Log4j2
@SpringBootApplication
@ConfigurationPropertiesScan
public class InventarbuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarbuddyApplication.class, args);
    }
}
