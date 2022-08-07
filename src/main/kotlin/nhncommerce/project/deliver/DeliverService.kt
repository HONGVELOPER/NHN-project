package nhncommerce.project.deliver

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.deliver.domain.DeliverListViewDTO
import nhncommerce.project.exception.CustomException
import nhncommerce.project.exception.ErrorCode
import nhncommerce.project.deliver.domain.DeliverListDTO
import nhncommerce.project.deliver.domain.QDeliver
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.alertDTO
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.function.Function

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

    fun findDeliverListByUser(userId: Long, pageRequestDTO: PageRequestDTO): PageResultDTO<DeliverListDTO, Deliver> {
        val user: User = userRepository.findById(userId).get()
        val booleanBuilder = getSearch(pageRequestDTO, user)
        val pageable = pageRequestDTO.getPageable(Sort.by("deliverId").descending())
        val result = deliverRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Deliver, DeliverListDTO> =
            Function<Deliver, DeliverListDTO> { entity: Deliver? -> entityToDto(entity!!) }
        return PageResultDTO<DeliverListDTO, Deliver>(result, fn)
    }

    fun entityToDto(deliver: Deliver): DeliverListDTO {
        return DeliverListDTO(
            deliver.deliverId,
            deliver.addressName,
            deliver.address,
            deliver.name,
            deliver.phone,
            deliver.createdAt
        )
    }

    fun getSearch(pageRequestDTO: PageRequestDTO, user: User): BooleanBuilder {
        var type = pageRequestDTO.type
        var booleanBuilder = BooleanBuilder()
        var qDeliver = QDeliver.deliver
        var keyword = pageRequestDTO.keyword
        var expression = qDeliver.user.eq(user).and(qDeliver.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    fun getDeliverViewList(userId: Long):List<DeliverListViewDTO>{
        val list = mutableListOf<DeliverListViewDTO>()
        val user = userRepository.findByUserId(userId)
        val deliverList = deliverRepository.findByUser(user)
        deliverList.map {
            val deliverListDTO = DeliverListViewDTO(it.deliverId, it.addressName + " : " + it.address)
            list.add(deliverListDTO)
        }
        return list.toList()
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