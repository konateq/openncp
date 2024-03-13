package eu.europa.ec.sante.openncp.application.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "eu.europa.ec.sante")
public class ClientWsApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ClientWsApplication.class, args);
    }
}
