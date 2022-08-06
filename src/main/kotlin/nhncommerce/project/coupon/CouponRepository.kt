package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface CouponRepository : JpaRepository<Coupon,Long>, QuerydslPredicateExecutor<Coupon>{
    fun findByCouponId(couponId: Long): Coupon

}