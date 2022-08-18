package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.order.domain.Order
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class ReviewDTO (

    @field:NotBlank(message = "내용을 입력해주세요.")
    val content: String = "",

    @field:Positive(message = "별점을 입력해주세요.")
    val star: Int = 0,

    var reviewImage: String ?= null,

) {
    fun toEntity(order: Order): Review {
        return Review(
            reviewId = 0L,
            status = Status.ACTIVE,
            content = content,
            star = star,
            reviewImage = reviewImage,
            user = order.user,
            order = order,
            product = order.optionDetail.product,
            // 옵션 디테일과 상품이 nullable 한 관계 확인 필요.
            // 오더와 옵션 디테일 nullable 한 관계 확인 필요.
        )
    }

    fun updateReviewImage(newReviewImage: String?) {
        reviewImage = newReviewImage
    }

}