package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface DeliverRepository: JpaRepository<Deliver, Long>, QuerydslPredicateExecutor<Deliver> {

}