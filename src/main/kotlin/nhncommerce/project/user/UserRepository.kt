package nhncommerce.project.user

import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String) : User?
}