package nhncommerce.project.user.domain

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


data class PasswordDTO(

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String = "",

    @field:NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @field:Size(min = 6, max = 20, message = "비밀번호를 6자 이상 20자 이내로 입력해주세요")
    val newPassword: String = "",

    @field:NotBlank(message = "새로운 비밀번호를 한번 더 입력해주세요.")
    @field:Size(min = 6, max = 20, message = "비밀번호를 6자 이상 20자 이내로 입력해주세요")
    val newPasswordVerify: String = "",
) {}