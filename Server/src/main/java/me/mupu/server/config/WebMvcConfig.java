package me.mupu.server.config;

import me.mupu.server.HashPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public HashPasswordEncoder passwordEncoder() {
        return new HashPasswordEncoder();
    }

}