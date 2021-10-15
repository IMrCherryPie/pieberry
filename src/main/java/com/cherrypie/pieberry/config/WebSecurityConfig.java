package com.cherrypie.pieberry.config;

import com.cherrypie.pieberry.domain.User;
import com.cherrypie.pieberry.repo.UserDetailsRepo;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/").permitAll() // Позволяет заходить даже неавторизованным пользователям и смотреть некоторын данные
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

//    Сохранние авторизованного пользователя в базу данных
    @Bean
    public PrincipalExtractor principalExtractor(UserDetailsRepo userDetailsRepo){
        return map -> {
            String id = (String) map.get("sub");
            User user = userDetailsRepo.findById(id).orElseGet(() ->{
                User newUser = new User();
                newUser.setId(id);
                newUser.setName((String) map.get("name"));
                newUser.setEmail((String) map.get("email"));
                newUser.setUserpic((String) map.get("picture"));
                newUser.setLocale((String) map.get("locale"));
                newUser.setGender((String) map.get("gender"));

                return newUser;
            });

            user.setLastVisit(LocalDateTime.now());
            return userDetailsRepo.save(user);

        };
    }
}
