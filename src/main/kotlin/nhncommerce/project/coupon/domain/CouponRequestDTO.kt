package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import java.time.LocalDate
import javax.validation.constraints.*

data class CouponRequestDTO (
    var couponId : Long?=null,

    var user : User? = null,

    var status: Status = Status.ACTIVE,

    var couponName: String="",

    var discountRate: Int=0,

    var expired: LocalDate
)