package nhncommerce.project.security.service

import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
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
        println("oAuth2User : $oAuth2User")
        val provider = userRequest.clientRegistration.registrationId // google
        println("provider : $provider")
        val providerId = oAuth2User.attributes["sub"] as String
        println("provider ID : $providerId")
        val username = oAuth2User.attributes["name"] as  String
        val email = oAuth2User.attributes["email"] as String
        println("email : $email")
        val role = ROLE.ROLE_USER


        val user: User = userRepository.findByEmail(email)
            ?: userRepository.save(
                User(
                    name = username,
                    email = email,
                    role = role,
                    oauthId = providerId,
                    provider = provider,
                    gender = Gender.MALE,
                    phone = 123,
                    status = Status.ACTIVE
                )
            )

        return Oauth2LoginUserDetails(user, oAuth2User.attributes)
    }
}