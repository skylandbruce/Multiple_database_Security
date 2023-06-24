package com.sky.multidbsec;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@SpringBootApplication
public class MultidbsecApplication {
		private static final Logger log = LoggerFactory.getLogger(MultidbsecApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MultidbsecApplication.class, args);
	}
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner init_admin(@Qualifier("secondDataSource") DataSource secondDataSource) {
		
		return (args) -> {
		// init a admin
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(secondDataSource);

		if(!users.userExists("admin")){
			UserDetails admin = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("admin"))
				.roles("USER","ADMIN")
				.build();
			users.createUser(admin);
			log.info("Inited admin auth");
		}	

		// caution for protecting admin
		log.info("Should change password of admin");
		log.info("-------------------------------");
		
	  };
	  
	}	

}