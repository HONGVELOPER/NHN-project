package nhncommerce.project.coupon.domin

import nhncommerce.project.baseentity.Status
import java.time.LocalDateTime

class CouponDTO (

    val couponId : Long,

    var status:Status = Status.ACTIVE,

    var couponName : Int,

    var discountRate : String,

    var expired : LocalDateTime
)