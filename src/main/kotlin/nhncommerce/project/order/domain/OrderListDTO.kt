package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import java.time.LocalDateTime

data class OrderListDTO(
    val orderId: Long?,
    val status: Status= Status.ACTIVE,
    val price: Int,
    val phone: String,
    val user: User,
    val coupon: Coupon?=null,
    val optionDetail: OptionDetail,
    val deliver: Deliver,
    val createdAt : LocalDateTime,
    val updatedAt : LocalDateTime
)