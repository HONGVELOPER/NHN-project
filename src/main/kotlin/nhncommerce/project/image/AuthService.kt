package nhncommerce.project.image

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class AuthService {

    @Value("\${image.authUrl}")
    private var authUrl = ""

    @Value("\${image.tenantId}")
    private var tenantId = ""

    @Value("\${image.username}")
    private var username = ""

    @Value("\${image.password}")
    private var password = ""

    var tokenRequest = TokenRequest()
    var restTemplate = RestTemplate()

    inner class TokenRequest {
        var auth = Auth()

        inner class Auth {
            var tenantId: String = ""
            var passwordCredentials: PasswordCredentials = PasswordCredentials()
        }

        inner class PasswordCredentials {
            var username: String = ""
            var password: String = ""
        }
    }

    fun requestToken(): String? {

        tokenRequest.auth.tenantId = tenantId
        tokenRequest.auth.passwordCredentials.username = username
        tokenRequest.auth.passwordCredentials.password = password

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
        return requestToken()
    }

}