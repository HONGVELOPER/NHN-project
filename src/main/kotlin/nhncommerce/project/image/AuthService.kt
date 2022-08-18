package nhncommerce.project.image

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
@ConfigurationProperties(prefix = "image")
class AuthService {

    var authUrl = ""

    var tenantId = ""

    var username = ""

    var password = ""

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