package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank


data class PasswordDTO(

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String = "",

    @field:NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    val newPassword: String = "",

    @field:NotBlank(message = "새로운 비밀번호를 한번 더 입력해주세요.")
    val newPasswordVerify: String = "",
) {}