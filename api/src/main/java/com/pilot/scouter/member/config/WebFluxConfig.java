package com.pilot.scouter.member.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Log4j2
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.
                addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
            @Value("classpath:/static/index.html") Resource html) {
        return route(GET("/"), request
                -> ok().contentType(MediaType.TEXT_HTML).syncBody(html)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> staticRouter() {
        return RouterFunctions
                .resources("/**", new ClassPathResource("static/"));
    }

    @Bean
    public RouterFunction<ServerResponse> fileUpload() {
        RequestPredicate predicate = RequestPredicates.POST("/fileUpload").and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA));
        RouterFunction<ServerResponse> response = RouterFunctions.route(predicate, (request)->{
            request.queryParams(); //이러한 방식으로 시도를 해 봅니다..
            Mono<String> mapper = request.multipartData().map(it -> it.get("files"))
                    .flatMapMany(Flux::fromIterable)
                    .cast(FilePart.class)
                    .flatMap(it -> it.transferTo(Paths.get("upload/" + it.filename())))
                    .then(Mono.just("OK"));
            Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(BodyInserters.fromPublisher(mapper, String.class));
            return res;
        });
        return response;
    }

}
