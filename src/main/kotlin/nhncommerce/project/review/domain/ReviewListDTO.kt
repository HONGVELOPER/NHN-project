package nhncommerce.project.review.domain

import java.time.LocalDateTime

class ReviewListDTO(
    val email: String,

    val reviewId: Long,

    val content: String,

    val star: Int,

    val reviewImage: String?,

    val createdAt: LocalDateTime,
) {}