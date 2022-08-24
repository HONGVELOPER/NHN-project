package nhncommerce.project.deliver.domain

import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class DeliverDTO(

    @field:NotBlank(message = "이름을 입력해주세요.")
    @field:Size(max = 20, message = "이름을 20자 이내로 입력해주세요")
    val name: String = "",

    @field:NotBlank(message = "배송지 이름을 입력해주세요.")
    val addressName: String = "",

    @field:NotBlank(message = "주소를 입력해주세요.")
    val address: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    @field:Size(min = 10, max = 11, message = "전화번호를 10자에서 11자 이내로 입력해주세요")
    val phone: String = "",
) {

    fun dtoToEntity(user: User): Deliver {
        return Deliver(
            deliverId = 0L,
            name = name,
            addressName = addressName,
            address = address,
            phone = phone,
            status = Status.ACTIVE,
            user = user
        )
    }

}