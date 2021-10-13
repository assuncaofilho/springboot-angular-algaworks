package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration //Classe de configuração
@EnableWebSecurity
public class ResourceServerConfig extends WebSecurityConfigurerAdapter{
	
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.inMemoryAuthentication()
			.withUser("root").password("{noop}root").roles("ROLE");
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
	    
	    http.authorizeRequests()
	            .antMatchers("/categorias").permitAll()
	            .anyRequest().authenticated()
	        .and()
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	            .csrf().disable()
	            .oauth2ResourceServer().opaqueToken();
	}
	

}
