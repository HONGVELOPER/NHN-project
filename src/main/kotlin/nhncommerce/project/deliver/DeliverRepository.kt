package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface DeliverRepository: JpaRepository<Deliver, Long>, QuerydslPredicateExecutor<Deliver> {


    fun findByUser(user: User): List<Deliver>


}