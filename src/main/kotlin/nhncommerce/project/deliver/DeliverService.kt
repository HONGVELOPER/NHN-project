package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.AlertService
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletResponse

@Service
class DeliverService (
    val deliverRepository: DeliverRepository,
    val userRepository: UserRepository,
    val alertService: AlertService,
) {
    fun createDeliver(deliverDTO: DeliverDTO, userId: Long) {
        val user: User = userRepository.findById(userId).get()
        val deliver: Deliver = deliverDTO.toEntity(user)
        deliverRepository.save(deliver)
    }

    fun findDeliverById(deliverId: Long, userId: Long, response: HttpServletResponse): DeliverDTO {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        println("deliver user : ${deliver.user.userId}")
        if (deliver.user.userId != userId) {
            alertService.alertMessage("잘못된 접근입니다.", "/admin", response)
        }
        return DeliverDTO.fromEntity(deliver)
    }

//    fun findDeliverByUser(UserId: Long): MutableList<DeliverDTO> {
//        val deliverList: MutableList<Deliver> = deliverRepository.
//    }

    fun updateDeliver(
        userId: Long,
        deliverId: Long,
        deliverDTO: DeliverDTO,
        response: HttpServletResponse
    ): Deliver {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            alertService.alertMessage("잘못된 접근입니다.", "/admin", response)
        }
        deliver.update(deliverDTO)
        val updatedDeliver = deliverRepository.save(deliver)
        return updatedDeliver
    }

    fun deleteDeliver(
        userId: Long,
        deliverId: Long,
        response: HttpServletResponse,
    ) {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            alertService.alertMessage("잘못된 접근입니다.", "/admin", response)
        }
        deliverRepository.deleteById(deliverId)
    }
}