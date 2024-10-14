package com.example.external;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExternalApplication.class, args);
    }

    @GetMapping("ok")
    public void ok() {
        // nothing
    }
}
