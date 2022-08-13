package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.order.domain.Order
import nhncommerce.project.user.domain.User
import nhncommerce.project.user.domain.UserDTO
import java.io.InputStream
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class ReviewDTO (

    @field:NotBlank(message = "내용을 입력해주세요.")
    var content: String = "",

    @field:Positive(message = "별점을 입력해주세요.")
    var star: Int = 0,

    var reviewImage: String?,

//    var orderId: Long = 0L,

) {
//    fun toEntity(user: User, order: Order): Review { 주문 엮으면 바꿔야함.
//        return Review (
//            status = Status.ACTIVE,
//            content = content,
//            star = star,
//            reviewImage = reviewImage,
//            user = user,
//            order = order,
//        )
//    }
    fun toEntity(user: User): Review {
        return Review (
            status = Status.ACTIVE,
            content = content,
            star = star,
            reviewImage = reviewImage ?: "",
            user = user,
        )
    }

    companion object {
        fun fromEntity(review: Review): ReviewDTO {
            return review.run {
                ReviewDTO(
                    content = content,
                    star = star,
                    reviewImage = reviewImage,
                )
            }
        }
    }
}