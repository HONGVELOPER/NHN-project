package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import java.time.LocalDateTime

data class OrderListDTO(
    var orderId: Long?,
    var status: Status= Status.ACTIVE,
    var price: Int?=null,
    var phone: String?=null,
    var user: User?=null,
    var coupon: Coupon?=null,
    var optionDetail: OptionDetail?=null,
    var deliver: Deliver?=null,
    var createdAt : LocalDateTime,
    var updatedAt : LocalDateTime
)