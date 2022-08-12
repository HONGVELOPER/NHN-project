package nhncommerce.project.radis.domain

import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.radis.Constant.EventCoupon
import java.util.*

data class TimeCoupon (
    val eventCoupon: EventCoupon,
    val code : String = UUID.randomUUID().toString()
// 할인률 넣으면 좋을것
)