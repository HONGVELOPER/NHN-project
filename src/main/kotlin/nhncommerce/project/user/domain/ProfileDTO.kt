package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank

data class ProfileDTO (

    @field:NotBlank(message = "이메일을 입력해주세요.")
    var email: String = "",

    @field:NotBlank(message = "이름을 입력해주세요.")
    var name: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    var phone: String = "",

) {
    companion object {
        fun fromEntity(user: User): ProfileDTO {
            return user.run {
                ProfileDTO(
                    email = email,
                    name = name,
                    phone = phone,
                )
            }
        }
    }
}