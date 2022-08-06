package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.exception.CustomException
import nhncommerce.project.exception.ErrorCode
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.alertDTO
import org.springframework.stereotype.Service

@Service
class DeliverService (
    val deliverRepository: DeliverRepository,
    val userRepository: UserRepository,
) {
    fun createDeliver(deliverDTO: DeliverDTO, userId: Long) {
        val user: User = userRepository.findById(userId).get()
        val deliver: Deliver = deliverDTO.toEntity(user)
        if (deliver.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        deliverRepository.save(deliver)
    }

    fun findDeliverById(deliverId: Long, userId: Long): DeliverDTO {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        return DeliverDTO.fromEntity(deliver)
    }

    fun findDeliverByUser(userId: Long): List<Deliver> {
        val deliverList: List<Deliver> = deliverRepository.findByUserUserId(userId)
        deliverList.map {
            println(it)
        }
        return deliverList
    }

    fun updateDeliver(
        userId: Long,
        deliverId: Long,
        deliverDTO: DeliverDTO,
    ) {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        deliver.update(deliverDTO)
        deliverRepository.save(deliver)
    }

    fun deleteDeliverById(
        userId: Long,
        deliverId: Long,
    ) {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        deliverRepository.deleteById(deliverId)
    }
}