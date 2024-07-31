package com.springboot_authenication.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.springboot_authenication.security.OurUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
    private final OurUserDetailService userDetailService;

    public SecurityConfig(OurUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .requestMatchers("/user/**").hasAnyAuthority("USER")
                .requestMatchers("/assets/**").permitAll()
                
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(authenicationEntryPoint())
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler(customAuthenticationSuccesshandler())
                .failureHandler(authenticationhandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/signOut")
                .logoutSuccessUrl("/login")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.sendRedirect("/login");
                })
                .invalidateHttpSession(true)
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return new OurUserDetailService();
    }

    @Bean
    public AuthenticationEntryPoint authenicationEntryPoint() {
        return (request, response, authException) -> {
            response.sendRedirect("/auth/login");
        };
    }

    @Bean
    public AuthenticationProvider authenicationProvider() {
        DaoAuthenticationProvider daoAuthenicationProvider = new DaoAuthenticationProvider();
        daoAuthenicationProvider.setUserDetailsService(userDetailService);
        daoAuthenicationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenicationProvider;
    }

    @Bean
    public AuthenticationFailureHandler authenticationhandler() {
        return (request, response, exception) -> {
            String errorMessage = "Incorrect email or password.";
            request.getSession().setAttribute("error", errorMessage);
            response.sendRedirect("/auth/login?error");
        };
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccesshandler() {
        return (request, response, authentication) -> {
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (authorities.contains("ADMIN")) {
                response.sendRedirect("/admin/dashboard");
            } else if (authorities.contains("USER")) {
                response.sendRedirect("/user/dashboard");
            } else {
                response.sendRedirect("/home");
            }
        };
    }
}
