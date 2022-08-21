package nhncommerce.project.deliver

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.deliver.domain.DeliverListViewDTO
import nhncommerce.project.deliver.domain.DeliverListDTO
import nhncommerce.project.deliver.domain.QDeliver
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.alertDTO
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function

@Service
class DeliverService (
    val deliverRepository: DeliverRepository,
    val userRepository: UserRepository,
) {
    @Transactional
    fun createDeliver(deliverDTO: DeliverDTO, userId: Long) {
        val user: User = userRepository.findById(userId).get()
        val deliver: Deliver = deliverDTO.dtoToEntity(user)
        if (deliver.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        deliverRepository.save(deliver)
    }

    fun findDeliverById(deliverId: Long, userId: Long): DeliverDTO {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        return deliver.entityToDeliverDto()
    }

    fun findDeliverListByUser(userId: Long, pageRequestDTO: PageRequestDTO): PageResultDTO<DeliverListDTO, Deliver> {
        val deliverListBuilder = deliverListBuilder(pageRequestDTO, userId)
        val pageable = pageRequestDTO.getPageable(Sort.by("deliverId").descending())
        val result = deliverRepository.findAll(deliverListBuilder, pageable)
        val fn: Function<Deliver, DeliverListDTO> =
            Function<Deliver, DeliverListDTO> { entity: Deliver? -> entity?.entityToDeliverListDto() }
        return PageResultDTO<DeliverListDTO, Deliver>(result, fn)
    }

    fun deliverListBuilder(pageRequestDTO: PageRequestDTO, userId: Long): BooleanBuilder {
        val booleanBuilder = BooleanBuilder()
        val qDeliver = QDeliver.deliver
        val expression = qDeliver.user.userId.eq(userId).and(qDeliver.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    fun getDeliverViewList(userId: Long):List<DeliverListViewDTO>{
        val user = userRepository.findById(userId).get()
        val deliverList = deliverRepository.findByUser(user)
        return deliverList.map {
            DeliverListViewDTO(it.deliverId, "${it.addressName} : ${it.address}")
        }
    }

    @Transactional
    fun updateDeliver(
        userId: Long,
        deliverId: Long,
        deliverDTO: DeliverDTO,
    ) {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        deliver.update(deliverDTO)
    }

    @Transactional
    fun deleteDeliverById(
        userId: Long,
        deliverId: Long,
    ) {
        val deliver: Deliver = deliverRepository.findById(deliverId).get()
        if (deliver.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        deliver.updateStatus(Status.IN_ACTIVE)
    }
}