package nhncommerce.project.order

import nhncommerce.project.order.domain.Order
import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order> {

    fun findByUserAndOrderId(user: User,orderId: Long) : Optional<Order>

    fun findByOrderId(orderId: Long):Optional<Order>


}