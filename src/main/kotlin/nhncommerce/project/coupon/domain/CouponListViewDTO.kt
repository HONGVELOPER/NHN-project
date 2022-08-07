package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.Status


data class CouponListViewDTO (
    var couponId : Long? = null,
    var couponName : String? = null,
    var status: Status
)