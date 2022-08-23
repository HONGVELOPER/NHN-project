package nhncommerce.project.security

import nhncommerce.project.security.service.FormLoginUserService
import nhncommerce.project.security.service.Oauth2LoginUserService
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.HttpSessionEventPublisher


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val formLoginUserService: FormLoginUserService,
    val oauth2LoginUserService: Oauth2LoginUserService,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    @Bean
    fun httpSessionEventPublisher(): ServletListenerRegistrationBean<HttpSessionEventPublisher> {
        return ServletListenerRegistrationBean<HttpSessionEventPublisher>(HttpSessionEventPublisher());
    }

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/api/**").hasAnyRole("USER", "ADMIN")
            .anyRequest()
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedPage("/login")
            .and()
            .formLogin()
            .usernameParameter("username")
            .passwordParameter("password")
            .and()
            .userDetailsService(formLoginUserService)
            .oauth2Login()
            .defaultSuccessUrl("/")
            .userInfoEndpoint()
            .userService(oauth2LoginUserService)
            .and()
            .and()
            .logout()
            .logoutSuccessUrl("/products")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .and()
            .sessionManagement()
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())
            .expiredUrl("/api/users/sessionExpired")
            .and()
            .and()
            .build()
    }
}