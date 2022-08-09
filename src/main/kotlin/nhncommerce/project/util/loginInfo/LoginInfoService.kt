package nhncommerce.project.util.loginInfo

import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class LoginInfoService {

    fun getUserIdFromSession(): LoginInfoDTO {
        val loginInfo = LoginInfoDTO()
        val auth = SecurityContextHolder.getContext().authentication.principal
        if (auth == "anonymousUser") {
            loginInfo.isLogin = false
            return loginInfo
        } else {
            loginInfo.isLogin = true
            if (SecurityContextHolder.getContext().authentication.authorities.toList()[0].toString() == "ROLE_ADMIN") {
                loginInfo.isAdmin = true
            }
            val loginStatus = auth.javaClass.toString().split(".")[4]
            if (loginStatus == "FormLoginUserDetails") {
                val formLoginUserDetails: FormLoginUserDetails = auth as FormLoginUserDetails
                loginInfo.userId = formLoginUserDetails.getId()
            } else {
                val oAuth2LoginUserDetails: Oauth2LoginUserDetails = auth as Oauth2LoginUserDetails
                loginInfo.userId = oAuth2LoginUserDetails.getId()
            }
            return loginInfo
        }
    }
}