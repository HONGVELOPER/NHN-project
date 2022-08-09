package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.user.domain.User

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface CouponRepository : JpaRepository<Coupon,Long>, QuerydslPredicateExecutor<Coupon>{
    @Query("select c from Coupon as c where c.expired < :expired and c.user = :user order by c.status")
    fun findCouponsByUser(@Param("user")user : User,@Param("expired") expired : LocalDate) : List<Coupon>
}