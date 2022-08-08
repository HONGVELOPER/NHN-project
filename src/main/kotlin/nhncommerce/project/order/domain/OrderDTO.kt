package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.user.domain.User
import java.util.*

data class OrderDTO(
    var status: Status?= Status.ACTIVE,
    var price: Int?=null,
    var phone: String?=null,
    var user: User?=null,
    var coupon: Coupon?=null,
    var optionDetail: OptionDetail? =null,
    var deliver: Deliver?=null
){
    fun toEntity(): Order{
        return Order(
            status = status,
            price = price,
            phone = phone,
            user = user,
            coupon = coupon,
            optionDetail = optionDetail,
            deliver = deliver
        )
    }
}