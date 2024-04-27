package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/", "/home").permitAll()
                        .anyRequest()
                        .hasAnyAuthority(Role.ADMIN.toString())
                )
                .oauth2Login();

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

                        grantedAuthorities.add((GrantedAuthority) () -> createUser.getRole().toString());
                        return createUser;
                    });
                }
            });
            return grantedAuthorities;
        };
    }
}