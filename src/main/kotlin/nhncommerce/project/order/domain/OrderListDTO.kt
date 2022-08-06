package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import java.time.LocalDateTime

data class OrderListDTO(
    var orderId: Long?,
    var status: Status= Status.ACTIVE,
    var price: Int?=null,
    var phone: String?=null,
    var userId: User?=null,
    var couponId: Coupon?=null,
    var productId: Product?=null,
    var createdAt : LocalDateTime
)