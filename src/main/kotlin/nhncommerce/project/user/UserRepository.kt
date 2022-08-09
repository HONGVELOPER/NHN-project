package nhncommerce.project.user

import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface UserRepository : JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    fun findByEmail(email: String) : User?

    fun findByUserId(userId: Long) : User

}