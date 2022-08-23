package nhncommerce.project.security.domain

import nhncommerce.project.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable

class Oauth2LoginUserDetails (
    private val user: User,
    private val attributes: Map<String, Any>
) : OAuth2User, Serializable { //Serializable 추가

    fun getId(): Long = user.userId

    override fun getName(): String {
        return attributes["email"].toString()
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collect: MutableCollection<GrantedAuthority> = ArrayList()
        collect.add(GrantedAuthority { user.role.toString() })
        return collect
    }

    override fun hashCode(): Int {
        return attributes["email"].toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        println("oauth login 진입")
        if (this.toString() == other.toString()) return true
        if (javaClass != other?.javaClass) return false

        other as Oauth2LoginUserDetails
        if (this.attributes["sub"] == other.attributes["sub"]) return true
        return false
    }
}
