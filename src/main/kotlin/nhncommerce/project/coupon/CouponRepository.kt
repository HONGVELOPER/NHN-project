package nhncommerce.project.coupon

import nhncommerce.project.coupon.domin.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon,Long>