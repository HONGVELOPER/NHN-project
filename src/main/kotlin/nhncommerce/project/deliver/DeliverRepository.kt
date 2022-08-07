package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface DeliverRepository: JpaRepository<Deliver, Long> {

    fun findByUserUserId(userId: Long): List<Deliver>


    fun findByUser(user: User): List<Deliver>


}