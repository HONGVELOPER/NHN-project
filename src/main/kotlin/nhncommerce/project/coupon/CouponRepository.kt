package nhncommerce.project.coupon

import com.querydsl.core.types.Predicate
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param

interface CouponRepository : JpaRepository<Coupon,Long>, QuerydslPredicateExecutor<Coupon>{

    fun findByCouponId(couponId: Long): Coupon

}