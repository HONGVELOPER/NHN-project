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
            .antMatchers("/admin/**").hasRole("ADMIN") // 어드민일경우
            .antMatchers("/api/**").hasAnyRole("USER", "ADMIN") // 로그인 사람
            .anyRequest()
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedPage("/login")
            .and()
            .formLogin()
//            .loginPage("/login") -> 커스텀 login 페이지 제작시 페이지 주소 넣어야함.
//            .defaultSuccessUrl("/") ->  로그인 성공시 라우팅 되는 default 경로 (수정 가능)
//            .failureUrl("/login") // -> 로그인 실패시 라우팅 되는 default 경로 (수정 가능)
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
//            .maxSessionsPreventsLogin(true) // session 중복 로그인 처리
            .sessionRegistry(sessionRegistry())
            .expiredUrl("/api/users/sessionExpired")
//            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // default session 생성 방식
            .and()
            .and()
            .build()
    }
}