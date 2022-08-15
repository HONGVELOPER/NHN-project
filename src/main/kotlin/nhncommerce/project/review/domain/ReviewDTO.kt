package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.order.domain.Order
import nhncommerce.project.order.domain.OrderDTO
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

) {
    fun toEntity(order: Order): Review { // 주문 엮으면 바꿔야함.
        return Review (
            status = Status.ACTIVE,
            content = content,
            star = star,
            reviewImage = reviewImage ?: "",
            user = order.user,
            order = order,
            product = order.optionDetail?.product!!,
            // 옵션 디테일과 상품이 nullable 한 관계 확인 필요.
            // 오더와 옵션 디테일 nullable 한 관계 확인 필요.
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