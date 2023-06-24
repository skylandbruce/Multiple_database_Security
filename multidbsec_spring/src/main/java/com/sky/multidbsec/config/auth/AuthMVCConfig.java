package com.sky.multidbsec.config.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthMVCConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/home").setViewName("public/home");
		registry.addViewController("/").setViewName("public/home");
		registry.addViewController("/hello").setViewName("auth/hello");
		registry.addViewController("/login").setViewName("public/login");
		// registry.addViewController("/register").setViewName("public/registration");
		// registry.addViewController("/update-password").setViewName("auth/change-password");
		// registry.addViewController("/profile").setViewName("auth/profile");
	}

}