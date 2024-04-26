package eu.europa.ec.sante.openncp.application.transformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp"})
@EnableSwagger2
public class TranslationsAndMappingsApplication extends SpringBootServletInitializer {

    public static void main(String... args) {
        SpringApplication.run(TranslationsAndMappingsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder;
    }
}
