package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ProfileDTO (

    @field:Size(max = 20, message = "이름을 20자 이내로 입력해주세요")
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    @field:Size(max = 13, message = "전화번호를 13자 이내로 입력해주세요")
    val phone: String ?= null,

) {}