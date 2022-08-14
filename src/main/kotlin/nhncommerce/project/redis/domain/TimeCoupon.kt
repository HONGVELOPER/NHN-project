package nhncommerce.project.redis.domain

import nhncommerce.project.redis.constant.EventCoupon
import java.util.*

data class TimeCoupon (
    val eventCoupon: EventCoupon,
    val code : String = UUID.randomUUID().toString()
// 할인률 넣으면 좋을것
)