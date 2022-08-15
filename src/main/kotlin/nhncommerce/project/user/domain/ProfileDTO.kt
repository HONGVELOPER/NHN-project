package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank

data class ProfileDTO (

    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    val phone: String = "",

) {}