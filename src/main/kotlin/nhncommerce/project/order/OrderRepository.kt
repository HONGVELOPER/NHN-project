package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import nhncommerce.project.order.domain.Order
import nhncommerce.project.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

    fun findByuserIdAndOrderId(userId: User,orderId: Long) : Optional<Order>


}