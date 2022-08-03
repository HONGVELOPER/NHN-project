package nhncommerce.project.security

import nhncommerce.project.security.service.FormLoginUserService
import nhncommerce.project.security.service.Oauth2LoginUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val formLoginUserService: FormLoginUserService,
    val oauth2LoginUserService: Oauth2LoginUserService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.authorizeRequests()
//            .antMatchers("/api/**").hasRole("USER")
//            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest()
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedPage("/login")
            .and()
            .formLogin()
//            .loginPage("/login") -> 커스텀 login 페이지 제작시 페이지 주소 넣어야함.
//            .defaultSuccessUrl("/") ->  로그인 성공시 라우팅 되는 default 경로 (수정 가능)
//            .failureUrl("/login?error) -> 로그인 실패시 라우팅 되는 default 경로 (수정 가능)
            .usernameParameter("username")
            .passwordParameter("password")
            .and()
            .userDetailsService(formLoginUserService)
            .oauth2Login()
//            .loginPage("login")
            .defaultSuccessUrl("/")
            .userInfoEndpoint()
            .userService(oauth2LoginUserService)
            .and()
            .and()
            .logout()
            .and()
            .sessionManagement()
            .maximumSessions(1)
//            .maxSessionsPreventsLogin(true) // session 중복 로그인 처리
//            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // default session 생성 방식
            .and()
            .and()
            .build()
    }

}