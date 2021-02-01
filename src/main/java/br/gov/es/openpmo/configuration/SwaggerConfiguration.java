package br.gov.es.openpmo.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(apis()).paths(PathSelectors.any()).build()
                .apiInfo(apiInfo()).securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private Predicate<RequestHandler> apis() {
        return RequestHandlerSelectors.basePackage("br.gov.es.openpmo.controller");
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
        return Lists.newArrayList(new SecurityReference(AUTHORIZATION_HEADER, new AuthorizationScope[0]));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("OPEN-PMO Backend")
                .contact(new Contact("Totem Treinamento & Consultoria em TI", "http://www.totemti.com.br/",
                        "atendimento@totemti.com.br"))
                .description("This API documentation is responsible for explaining how to use the methods developed for OPEN-PMO.")
                .version("1.0.0").build();
    }
}
