package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

data class OrderRequestDTO(
    val status: Status=Status.ACTIVE,
    val price: Int,
    val phone: String,
    val userId: Long,
    val couponId: Long?=null,
    val optionDetailId: Long,
    val count: Int,
    val deliverId: Long?=null
)