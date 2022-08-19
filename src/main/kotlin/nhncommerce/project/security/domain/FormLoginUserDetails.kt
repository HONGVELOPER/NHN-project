package nhncommerce.project.security.domain

import nhncommerce.project.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class FormLoginUserDetails(
    val user: User,
): UserDetails {
    fun getId(): Long = user.userId

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        arrayListOf(SimpleGrantedAuthority(user.role.name))

    override fun getPassword() = user.password

    override fun getUsername() = user.email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    override fun hashCode(): Int {
        return user.email.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this.toString() == other.toString()) return true
        if (javaClass != other?.javaClass) return false

        other as FormLoginUserDetails
        if (this.username == other.username) return true
        return false
    }
}