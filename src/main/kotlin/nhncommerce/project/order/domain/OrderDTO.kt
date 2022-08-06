package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User

data class OrderDTO(
    var orderId: Long?=null,
    var status: Status?= Status.ACTIVE,
    var price: Int?=null,
    var phone: String?=null,
    var userId: User?=null,
    var couponId: Coupon?=null,
    var productId: Product?=null
){
    fun toEntity(): Order{
        return Order(
            orderId = orderId,
            status = status,
            price = price,
            phone = phone,
            userId = userId,
            couponId = couponId,
            productId = productId
        )
    }
}