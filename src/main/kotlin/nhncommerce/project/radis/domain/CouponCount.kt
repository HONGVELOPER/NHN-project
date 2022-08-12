package nhncommerce.project.radis.domain

import nhncommerce.project.radis.Constant.EventCoupon
import java.time.LocalDate

data class CouponCount (
    var eventCoupon : EventCoupon?,
    var discount : Int?=0,
    var limit : Int?,
    var expired : LocalDate?,
    var progress : Boolean?
) {
    fun decrease(){
        this.limit = this.limit!! - 1
    }

    fun end() : Boolean {
        return limit == 0 || limit == null
    }
}