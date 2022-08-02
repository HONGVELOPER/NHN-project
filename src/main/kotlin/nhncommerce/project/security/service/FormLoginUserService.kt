package nhncommerce.project.security.service

import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FormLoginUserService(
    val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        println("user name : $email")
        println("user detail service 접근")
        val formLoginUserDetails: FormLoginUserDetails? = userRepository.findByEmail(email)?.let { FormLoginUserDetails(it) }
        return formLoginUserDetails ?: throw UsernameNotFoundException("loadUserByUsername() - cannot find username:$email)")
    }
}