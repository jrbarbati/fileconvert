package com.thinktechnologies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@RestController
public class SwaggerConfig
{
    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(metadata());
    }

    private ApiInfo metadata()
    {
        return new ApiInfoBuilder()
                .title("File Convert")
                .description("Converts files from one file type to another using CloudConverApi")
                .build();
    }

    @GetMapping(value = {"/swagger", "/"})
    public @ResponseBody
    ModelAndView swaggerui()
    {
        return new ModelAndView("redirect:swagger-ui.html");
    }
}
