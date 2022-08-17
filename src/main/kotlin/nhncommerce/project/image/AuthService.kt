package nhncommerce.project.image

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class AuthService {


    private var authUrl = "https://api-identity.infrastructure.cloud.toast.com/v2.0"
    private var tenantId = "507cc2a432bc43de8721f24810f3daa1"
    private var username = "soonbum-jeong@nhn-commerce.com"
    private var password = "1234"

    var tokenRequest = TokenRequest()
    var restTemplate = RestTemplate()

    inner class TokenRequest {
        var auth = Auth()

        inner class Auth {
            var tenantId: String? = null
            var passwordCredentials: PasswordCredentials = PasswordCredentials()
        }

        inner class PasswordCredentials {
            var username: String? = null
            var password: String? = null
        }
    }

    init {
        // 요청 본문 생성
        tokenRequest = TokenRequest()
        tokenRequest.auth.tenantId = tenantId
        tokenRequest.auth.passwordCredentials.username = username
        tokenRequest.auth.passwordCredentials.password = password
        restTemplate = RestTemplate()
    }

    fun requestToken(): String? {
        val identityUrl = "$authUrl/tokens"

        // 헤더 생성
        val headers = org.springframework.http.HttpHeaders()
        headers.add("Content-Type", "application/json")
        val httpEntity: HttpEntity<TokenRequest> = HttpEntity<TokenRequest>(tokenRequest, headers)

        // 토큰 요청
        val response = restTemplate.exchange(
            identityUrl, HttpMethod.POST, httpEntity,
            String::class.java
        )
        return response.body
    }

    fun generateToken(): String? {
        val authService = AuthService()
        return authService.requestToken()
    }

}