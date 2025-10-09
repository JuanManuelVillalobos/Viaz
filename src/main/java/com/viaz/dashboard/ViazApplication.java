package com.viaz.dashboard;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.TimeZone;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ViazApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViazApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Set the default timezone for the JVM to UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
