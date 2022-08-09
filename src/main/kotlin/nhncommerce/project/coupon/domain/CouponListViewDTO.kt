package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.Status
import java.time.LocalDate


data class CouponListViewDTO (
    var couponId : Long? = null,
    var couponName : String? = null,
    var expired: LocalDate,
    var status: Status
)