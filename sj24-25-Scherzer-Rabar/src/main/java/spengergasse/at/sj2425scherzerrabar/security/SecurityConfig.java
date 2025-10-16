package spengergasse.at.sj2425scherzerrabar.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager(User.withUsername("jaaron")
                .password("{noop}password") // {noop} heißt: kein Passwort-Encoding
                .authorities("read")
                .build()
        );
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiTokenFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/token")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()) // Basic Auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiJwtFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2Configurer -> oauth2Configurer.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }




    @Bean
    @Order(3)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
       return http.csrf(Customizer.withDefaults())
               .authorizeHttpRequests(authorize->authorize
                       .requestMatchers("/").permitAll()
                       .requestMatchers("/login").permitAll()
                       .requestMatchers("/error").permitAll()
                       .requestMatchers("/logout/**").permitAll()
                       .anyRequest().authenticated())
               .formLogin(formLogin->formLogin.defaultSuccessUrl("/",true))
               .httpBasic(AbstractHttpConfigurer::disable)
               .build();
    }


}
