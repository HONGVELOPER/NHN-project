package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.ROLE
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AdminProfileDTO (

    @field:Size(max = 30, message = "이름을 30자 이내로 입력하세요")
    @field:NotBlank(message = "이름을 입력해주세요.")
    var name: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    var phone: String = "",

    @field:NotBlank(message = "권한을 선해주세요.")
    var role: String,

    ) {
    companion object {
        fun fromEntity(user: User): AdminProfileDTO {
            return user.run {
                AdminProfileDTO(
                    name = name,
                    phone = phone,
                    role = role.name,
                )
            }
        }
    }
}