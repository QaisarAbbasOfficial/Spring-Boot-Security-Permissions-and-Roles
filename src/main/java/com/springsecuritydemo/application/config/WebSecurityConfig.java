package com.springsecuritydemo.application.config;

import com.springsecuritydemo.application.filters.JWTAuthenticationFilter;
import com.springsecuritydemo.application.filters.JWTAuthorizationFilter;
import com.springsecuritydemo.application.security.JwtAuthenticationEntryPoint;
import com.springsecuritydemo.application.security.MyBCryptPasswordEncoder;
import com.springsecuritydemo.services.implementations.UserDetailsServiceImpl;
import com.springsecuritydemo.application.utility.JwtOutils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl userDetailsService;
    private MyBCryptPasswordEncoder myBCryptPasswordEncoder;
    private final JwtOutils jwtOutils;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, MyBCryptPasswordEncoder myBCryptPasswordEncoder, JwtOutils jwtOutils, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.myBCryptPasswordEncoder = myBCryptPasswordEncoder;
        this.jwtOutils = jwtOutils;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/sign-up", "/sign-in").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                //utiliser pour le login :: post localhost:8081/login
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtOutils))
                // utliser pour verifier le token et extraitre les premissions
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtOutils))
                // this disables session creation on Spring Security
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(myBCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
