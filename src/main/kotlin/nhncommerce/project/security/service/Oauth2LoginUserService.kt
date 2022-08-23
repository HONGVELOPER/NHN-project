package nhncommerce.project.security.service

import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class Oauth2LoginUserService(
    val userRepository: UserRepository
) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User = super.loadUser(userRequest)
        val provider = userRequest.clientRegistration.registrationId // google
        val oauthId = oAuth2User.attributes["sub"] as String
        val username = oAuth2User.attributes["name"] as String
        val email = oAuth2User.attributes["email"] as String
        val role = ROLE.ROLE_USER


        val user: User? = userRepository.findByEmail(email)
        return if (user != null) {
            when (user.status == Status.ACTIVE) {
                true -> Oauth2LoginUserDetails(user, oAuth2User.attributes)
                false -> throw UsernameNotFoundException("탈퇴한 회원입니다.")
            }
        } else {
            val singUpUser = userRepository.save(
                User(
                    userId = 0L,
                    name = username,
                    email = email,
                    role = role,
                    oauthId = oauthId,
                    provider = provider,
                    status = Status.ACTIVE
                )
            )
            Oauth2LoginUserDetails(singUpUser, oAuth2User.attributes)
        }
    }
}