package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.Status
import java.time.LocalDate


data class CouponListViewDTO (
    val couponId : Long? = null,
    val couponName : String? = null,
    val expired: LocalDate,
    val status: Status
)