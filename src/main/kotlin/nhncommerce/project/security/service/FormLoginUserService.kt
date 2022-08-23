package nhncommerce.project.security.service

import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FormLoginUserService(
    val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User? = userRepository.findByEmail(email)
        return if (user != null) {
            when (user.status == Status.ACTIVE) {
                true -> FormLoginUserDetails(user)
                false -> throw UsernameNotFoundException("탈퇴한 회원입니다.")
            }
        } else {
            throw UsernameNotFoundException("loadUserByUsername() - cannot find username:$email)")
        }
    }
}