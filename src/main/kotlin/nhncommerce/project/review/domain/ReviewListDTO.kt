package nhncommerce.project.review.domain

import java.time.LocalDateTime

class ReviewListDTO(
    val reviewId: Long,

    val content: String,

    val star: Int,

    val createdAt: LocalDateTime,
) {}