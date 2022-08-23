package nhncommerce.project.review

import nhncommerce.project.order.domain.Order
import nhncommerce.project.product.domain.Product
import nhncommerce.project.review.domain.Review
import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param

interface ReviewRepository : JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review> {

    @Query("SELECT Count(r) FROM Review r WHERE r.status='ACTIVE' and r.product= :product")
    fun findActiveReviewCountByProduct(@Param("product") product: Product): Int
    
    fun findByOrder(order: Order) :Review

    fun findByUser(user: User): List<Review>


}