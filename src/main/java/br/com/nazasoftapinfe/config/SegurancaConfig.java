package br.com.nazasoftapinfe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class SegurancaConfig /*extends WebSecurityConfigurerAdapter*/ {
/*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser("wareline").password("2R8D8<NN!O").roles("ADMIN");

    }

    @Override
    protected void configure(HttpSecurity http)throws Exception{
      http
              .authorizeHttpRequests()
              .anyRequest().authenticated()
              .and()
              .httpBasic()
              .and()
              .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              ;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

 */
}
