package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/home")).hasAnyAuthority("USER")
                        .requestMatchers(new AntPathRequestMatcher("/home3")).hasAnyAuthority("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/login", "/error"))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(o2 -> o2
                        .defaultSuccessUrl("/home2")
                        .userInfoEndpoint(ui -> ui
                                .userAuthoritiesMapper(get())));
        return http.build();
    }


    private GrantedAuthoritiesMapper get() {
        Faker faker = new Faker();
        faker.internet().password();
        String password = faker.toString();

        return authorities -> {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OAuth2UserAuthority oAuth2UserAuthority) {
                    Map<String, Object> userAttributes = oAuth2UserAuthority.getAttributes();
                    String login = (String) userAttributes.get("login");
                    User user = userRepository.findByLogin(login).orElseGet(() -> {
                        User createUser = User.builder()
                                .login((String) userAttributes.get("login"))
                                .password(password)
                                .role(Role.USER)
                                .build();
                        userRepository.save(createUser);

                        return createUser;
                    });
                    grantedAuthorities.add((GrantedAuthority) () -> user.getRole().toString());
                }
            });
            return grantedAuthorities;
        };
    }
}