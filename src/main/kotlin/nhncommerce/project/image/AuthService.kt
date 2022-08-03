package nhncommerce.project.image

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate


class AuthService(var authUrl: String, tenantId: String?, username: String?, password: String?) {
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

    var tokenRequest: TokenRequest
    var restTemplate: RestTemplate

    init {

        // 요청 본문 생성
        tokenRequest = TokenRequest()
        tokenRequest.auth.tenantId = tenantId
        tokenRequest.auth.passwordCredentials.username = username
        tokenRequest.auth.passwordCredentials.password = password
        restTemplate = RestTemplate()
    }

    fun requestToken(): String? {
        var identityUrl = authUrl + "/tokens"

        // 헤더 생성
        var headers = org.springframework.http.HttpHeaders()
        headers.add("Content-Type", "application/json")
        var httpEntity: HttpEntity<TokenRequest> = HttpEntity<TokenRequest>(tokenRequest, headers)

        // 토큰 요청
        var response = restTemplate.exchange(
            identityUrl, HttpMethod.POST, httpEntity,
            String::class.java
        )
        return response.body
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var authUrl = "https://api-identity.infrastructure.cloud.toast.com/v2.0"
            var tenantId = "507cc2a432bc43de8721f24810f3daa1"
            var username = "soonbum-jeong@nhn-commerce.com" //NHN Cloud Account
            var password = "1234"
            var authService = AuthService(authUrl, tenantId, username, password)
            var token = authService.requestToken()
            println(token)
        }
    }
}