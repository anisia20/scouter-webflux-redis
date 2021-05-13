package com.pilot.scouter.member.config;

import com.pilot.scouter.common.command.JwtCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static io.swagger.models.auth.In.HEADER;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    JwtCmd jwtCmd;

    @Bean
    public Docket api() {
        Docket api = new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(singletonList(new ApiKey("JWT", AUTHORIZATION,  HEADER.name())))
                .securityContexts(singletonList(
                        SecurityContext.builder()
                                .securityReferences(
                                        singletonList(SecurityReference.builder()
                                                .reference("JWT")
                                                .scopes(new AuthorizationScope[0])
                                                .build()
                                        )
                                )
                                .build())
                )
                .select()
                .apis(RequestHandlerSelectors.any()) // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
                .paths(PathSelectors.ant("/v1/**")) // 그중 /api/** 인 URL들만 필터링
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false) // default response description 사용하지 않음

                ;

        return api;
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API")
                .description("API")
                .license("glegend")
                .version("v1")
                .build();
    }






}

