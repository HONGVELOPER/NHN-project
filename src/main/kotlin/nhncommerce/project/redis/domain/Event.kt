package nhncommerce.project.redis.domain

import nhncommerce.project.redis.constant.EventCoupon
import java.time.LocalDate

data class Event (
    val eventCoupon : EventCoupon?,
    val discount : Int=0,
    var limit : Int = 0,
    var expired : LocalDate= LocalDate.now(),
    var progress : Int = 0
) {
    fun decrease(){
        limit -= 1
    }

    fun end() : Boolean {
        return limit == 0 || limit == null
    }
}