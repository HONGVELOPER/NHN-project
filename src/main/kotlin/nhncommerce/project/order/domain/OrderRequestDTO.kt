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
    var phone: String,
    var userId: Long,
    var couponId: Long?=null,
    var optionDetailId: Long,
    var deliverId: Long?=null
)