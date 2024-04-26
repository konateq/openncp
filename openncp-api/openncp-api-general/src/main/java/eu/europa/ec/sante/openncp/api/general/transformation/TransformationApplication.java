package eu.europa.ec.sante.openncp.api.general.transformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"eu.europa.ec.sante.openncp.api.general.transformation"})
@EnableSwagger2
public class TransformationApplication extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TransformationApplication.class);

    public static void main(String... args) {
        SpringApplication.run(TransformationApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //return configureApplication(builder);
        return builder;
    }

    //private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
    //  return builder.sources(TranslationsAndMappingsApplication.class).bannerMode(Banner.Mode.OFF);
    //}
}
