package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface CouponRepository : JpaRepository<Coupon,Long>, QuerydslPredicateExecutor<Coupon>{

    fun findByCouponId(couponId: Long): Coupon

    fun findByUser(user: User): List<Coupon>

}