package com.rblbank.logging.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	
	@Override
	  protected void configure(HttpSecurity http) throws Exception {
		 http.authorizeRequests()
         .antMatchers("/").hasAnyRole("USER","ADMIN")
         .anyRequest().authenticated()
         .and()
         .formLogin().permitAll()
         .and()
         .logout().permitAll()
         .and()
         .exceptionHandling().accessDeniedPage("/403")
 ;

 http.csrf().disable();
	  }

	@Bean
	public HttpFirewall defaultHttpFirewall() {
	    return new DefaultHttpFirewall();
	}
	

}
