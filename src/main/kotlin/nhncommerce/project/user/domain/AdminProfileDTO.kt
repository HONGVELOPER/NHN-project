package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AdminProfileDTO (

    @field:NotBlank(message = "이름을 입력해주세요.")
    @field:Size(max = 20, message = "이름을 20자 이내로 입력해주세요")
    val name: String,

    @field:NotBlank(message = "전화번호를 입력해주세요.")
    @field:Size(min = 10, max = 11, message = "전화번호를 10자에서 11자 이내로 입력해주세요")
    val phone: String,

    @field:NotBlank(message = "권한을 선해주세요.")
    val role: String,

) {}