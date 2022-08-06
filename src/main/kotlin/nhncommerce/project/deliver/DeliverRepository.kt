package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import org.springframework.data.jpa.repository.JpaRepository

interface DeliverRepository: JpaRepository<Deliver, Long> {

//    fun findByEmail
}