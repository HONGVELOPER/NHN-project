package nhncommerce.project.security.service

import nhncommerce.project.baseentity.Status
import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class FormLoginUserService(
    private val userRepository: UserRepository,
    private val loginInfoService: LoginInfoService,
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User? = userRepository.findByEmail(email)
        return if (user != null) {
            when (user.status == Status.ACTIVE) {
                true -> {
                    loginInfoService.expireUserSession(user.email)
                    FormLoginUserDetails(user)
                }
                false -> throw UsernameNotFoundException("탈퇴한 회원입니다.")
            }
        } else {
            throw UsernameNotFoundException("존재하지 않는 아이디 입니다.")
        }
    }
}