package nhncommerce.project.user.domain

import java.time.LocalDateTime

class UserListDTO(
    val userId: Long,

    val role: String,

    val email: String,

    val gender: String,

    val name: String,

    val phone: String? = "",

    val createdAt: LocalDateTime,

) {
}