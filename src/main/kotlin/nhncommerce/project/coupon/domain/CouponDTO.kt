package nhncommerce.project.coupon.domain


import nhncommerce.project.baseentity.Status
import java.time.LocalDateTime

data class CouponDTO (

    var status: Status? = Status.ACTIVE,

    var couponName: String,

    var discountRate : Float,

    var expired : LocalDateTime

)