package com.sky.multidbsec.config.auth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.RequestDispatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    PasswordEncoder passwordEncoder;
    
    // for test : h2-console 
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }      
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, @Qualifier("firstDataSource") DataSource firstDataSource, @Qualifier("secondDataSource") DataSource secondDataSource) throws Exception {
        JdbcUserDetailsManager usersDB1 = new JdbcUserDetailsManager(firstDataSource);
        JdbcUserDetailsManager usersDB2 = new JdbcUserDetailsManager(secondDataSource);
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home").permitAll()
				.requestMatchers("/regist").permitAll()
				.requestMatchers("/update-password").authenticated()
				.requestMatchers(HttpMethod.POST,"/unsubscribe/**").authenticated()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				// you can customize login form
				.loginPage("/login").permitAll()
                // .loginProcessingUrl("loginprocess").permitAll()
                // defalut .failureUrl("/login?error")
                .failureUrl("/login?error").permitAll()
                // you can customize login form
                // default .defaultSuccessUri("/hello?continue")
				.defaultSuccessUrl("/hello?continue",true)
                .failureHandler((request, response, exception) -> {
                    // 실패한 경우의 중단 처리 로직을 구현.
                    // 예를 들어, 실패 횟수 카운트, 로깅 등을 수행.
                    // 중단 처리 이후에 리다이렉션을 수행해야 한다면, 직접 리다이렉션 로직을 추가.

                    try{
                        if(usersDB2.userExists(request.getParameter("username"))){
                            UserDetails userDB2=usersDB2.loadUserByUsername(request.getParameter("username"));
                            if(passwordEncoder.matches(request.getParameter("password"),userDB2.getPassword())){    
                                usersDB1.createUser(userDB2);

                                // login 경로의 POST 요청을 다시 처리하기 위해 forward를 사용합니다.
                                RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
                                dispatcher.forward(request, response);
                            }
                            else{
                                response.sendRedirect("/login?error");    
                            }
                        }
                        else{
                            response.sendRedirect("/login?error");
                        }

                    }catch (Exception e){
                        System.out.println("Error : Cacheing Fail -----");
                        System.out.println(e);
                        response.sendRedirect("/home");
                    }
                }).permitAll()
                
			)
			.logout((logout) -> logout.permitAll()
			);
			// .passwordManagement((management) -> management
			//     .changePasswordPage("/change-password")
			// );

		return http.build();
	}

    
}

