package nhncommerce.project.util.loginInfo

import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.session.FindByIndexNameSessionRepository
import org.springframework.session.Session
import org.springframework.session.data.redis.RedisOperationsSessionRepository
import org.springframework.session.security.SpringSessionBackedSessionRegistry
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class LoginInfoService(
    private val findByIndexNameSessionRepository: FindByIndexNameSessionRepository<*>,
) {

    fun getUserIdFromSession(): LoginInfoDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication.principal == ANONYMOUS_USER) {
            LoginInfoDTO(isLogin = false)
        } else {
            val isAdmin = authentication.authorities.toList().first().toString() == "ROLE_ADMIN"
            val loginStatus = authentication.principal.javaClass.toString().split(".")[INDEX_OF_LOGIN_STATUS]

            val userId = if (loginStatus == FORM_LOGIN_USER) {
                (authentication.principal as FormLoginUserDetails).getId()
            } else {
                (authentication.principal as Oauth2LoginUserDetails).getId()
            }

            LoginInfoDTO(
                isLogin = true,
                userId = userId,
                isAdmin = isAdmin
            )
        }
    }

    fun expireUserSession(expireUserEmail: String) {
        val mutableMap = findByIndexNameSessionRepository.findByIndexNameAndIndexValue(
            FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
            expireUserEmail
        )
        mutableMap.entries.map {
            findByIndexNameSessionRepository.deleteById(it.key);
        }
    }

    companion object {
        private const val INDEX_OF_LOGIN_STATUS = 4
        private const val ANONYMOUS_USER = "anonymousUser"
        private const val FORM_LOGIN_USER = "FormLoginUserDetails"
    }
}