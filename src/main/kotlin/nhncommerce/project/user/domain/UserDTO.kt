package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UserDTO (

    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String = "",

    @field:NotBlank(message = "성별을 선택해주세요.")
    val gender: String = "",

    @field:Size(max = 30, message = "이름을 30자 이내로 입력하세요")
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String = "",

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Size(max = 30, message = "비밀번호를 30자 이내로 입력하세요")
    val password: String = "",

    @field:NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    @field:Size(max = 30, message = "비밀번호를 30자 이내로 입력하세요")
    val passwordVerify: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    val phone: String = "",

    val provider: String = "",
) {
    fun toEntity(): User {
        val genderStatus: Gender = if (gender == "MALE") {
            Gender.MALE
        } else {
            Gender.FEMALE
        }
        return User(
            email = email,
            gender = genderStatus,
            name = name,
            password = password,
            phone = phone,
            role = ROLE.ROLE_USER,
            status = Status.ACTIVE,
        )
    }
}