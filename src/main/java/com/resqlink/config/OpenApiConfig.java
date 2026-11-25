package com.resqlink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ResQLink - Emergency Resource Coordination API")
                        .version("1.0.0")
                        .description("Comprehensive REST API for disaster incident management, emergency aid requests, multi-criteria allocation, and transport tracking.")
                        .contact(new Contact()
                                .name("ResQLink Disaster Response Engineering")
                                .email("operations@resqlink.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
