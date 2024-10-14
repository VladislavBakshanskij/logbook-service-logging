package com.example.logbookservicelogging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;
import org.zalando.logbook.spring.webflux.LogbookExchangeFilterFunction;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class LogbookServiceLoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogbookServiceLoggingApplication.class, args);
    }

    @Bean
    @Scope("prototype")
    @LoadBalanced
    public RestClient.Builder restClientBuilder(Logbook logbook) {
        return RestClient.builder()
                .requestInterceptor(new LogbookClientHttpRequestInterceptor(logbook));
    }

    @Bean
    @Scope("prototype")
    @LoadBalanced
    public WebClient.Builder webClientBuilder(Logbook logbook) {
        return WebClient.builder()
                .filter(new LogbookExchangeFilterFunction(logbook));
    }

    @Bean
    @Scope("prototype")
    @LoadBalanced
    public RestTemplateBuilder restTemplateBuilder(Logbook logbook) {
        return new RestTemplateBuilder()
                .interceptors(new LogbookClientHttpRequestInterceptor(logbook));
    }

    @Bean
    public RestClient restClient(@LoadBalanced RestClient.Builder builder) {
        return builder.baseUrl("http://external").build();
    }

    @Bean
    public RestTemplate restTemplate(@LoadBalanced RestTemplateBuilder builder) {
        return builder.rootUri("http://external").build();
    }

    @Bean
    public WebClient webClient(@LoadBalanced WebClient.Builder builder) {
        return builder.baseUrl("http://external").build();
    }
}

@RestController
@RequestMapping("api")
record SimpleRestController(RestClient restClient, WebClient webClient, RestTemplate restTemplate) {
    @GetMapping("web-client")
    public Mono<ResponseEntity<Void>> webClientExample() {
        return webClient.get()
                .uri("ok")
                .retrieve()
                .toBodilessEntity();
    }

    @GetMapping("rest-client")
    public ResponseEntity<Void> restClientExample() {
        return restClient.get()
                .uri("ok")
                .retrieve()
                .toBodilessEntity();
    }

    @GetMapping("rest-template")
    public ResponseEntity<Void> restTemplateExample() {
        return restTemplate.getForEntity("ok", Void.class);
    }
}
