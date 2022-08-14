package nhncommerce.project.deliver.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import javax.validation.constraints.NotBlank

data class DeliverDTO(

    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String = "",

    @field:NotBlank(message = "배송지 이름을 입력해주세요.")
    val addressName: String = "",

    @field:NotBlank(message = "주소를 입력해주세요.")
    val address: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    val phone: String = "",
) {

    fun dtoToEntity(user: User): Deliver {
        return Deliver(
            name = name,
            addressName = addressName,
            address = address,
            phone = phone,
            status = Status.ACTIVE,
            user = user
        )
    }

//    companion object{
//        fun fromEntity(deliver: Deliver): DeliverDTO {
//            return deliver.run {
//                DeliverDTO(
//                    addressName = addressName,
//                    name = name,
//                    address = address,
//                    phone = phone ?: "",
//                )
//            }
//        }
//    }
}