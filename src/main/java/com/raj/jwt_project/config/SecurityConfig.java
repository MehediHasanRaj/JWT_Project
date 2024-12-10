package com.raj.jwt_project.config;

import com.raj.jwt_project.config.MyUserDetailsService;
import com.raj.jwt_project.config.jwt.AuthEntryPointJwt;
import com.raj.jwt_project.config.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // handaling the unauthorized request

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authorizeRequest -> authorizeRequest
                .requestMatchers("/api/signup").permitAll()
                .requestMatchers("/api/login").permitAll()
                .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable); // disable  csrf token, as we are not using session based.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults()); // this is turning on the basic user, pass checker.

        //jwt addition config
        http.exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler)); // exception handaling
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //we need to declare a bean, otherwise we can not use this in controller. error will be shown
    // .authenticate(UsenamePasswordAuthencationToken(username,password)) we can use to authenticate the user
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authmanager) throws Exception {
        return authmanager.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(myUserDetailsService);
        return authProvider;
    }

    @Bean // we are using this, to use globally
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
