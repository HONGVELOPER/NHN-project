package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

data class OrderRequestDTO(
    var status: Status=Status.ACTIVE,
    var price: Int,
    var phone: String?=null,
    var userId: Long?=null,
    var couponId: Long?=null,
    var optionDetailId: Long,
    @field:NotBlank(message = "불러올 배송지가 없니다")
    var deliverId: Long?=null
)