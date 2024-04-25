package com.khonsong.apis.khonsongapis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// @EnableWebMvc
// public class WebConfig implements WebMvcConfigurer {

// @Override
// public void addCorsMappings(CorsRegistry registry) {
// registry.addMapping("/**");
// }
// }

// @Configuration
// @EnableWebSecurity
// public class WebConfig extends WebSecurityConfigurerAdapter {
// @Override
// protected void configure(HttpSecurity http) throws Exception {
// http.cors().configurationSource(corsConfigurationSource());
// }
// @Bean
// CorsConfigurationSource corsConfigurationSource() {
// CorsConfiguration configuration = new CorsConfiguration();
// configuration.setAllowedOrigins(Arrays.asList("*"));
// configuration.setAllowedMethods(Arrays.asList("GET","POST"));
// configuration.setAllowedHeaders(Collections.singletonList("*"));
// UrlBasedCorsConfigurationSource source = new
// UrlBasedCorsConfigurationSource();
// source.registerCorsConfiguration("/**", configuration);
// return source;
// }
// }