package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.option.domain.OptionDetail
import nhncommerce.project.user.domain.User
import java.util.*

data class OrderDTO(
    val status: Status= Status.ACTIVE,
    val price: Int,
    val phone: String,
    val user: User,
    val coupon: Coupon?=null,
    val optionDetail: OptionDetail,
    val deliver: Deliver

){
    fun dtoToEntity(): Order{
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