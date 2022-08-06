package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UserDTO (

    @field:NotBlank(message = "이메일을 입력해주세요.")
    var email: String = "",

    @field:NotBlank(message = "성별을 선택해주세요.")
    var gender: String = "",

    @field:NotBlank(message = "이름을 입력해주세요.")
    var name: String = "",

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Size(max = 20, message = "비밀번호를 30자 이내로 입력하세요")
    var password: String = "",

    @field:NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    var passwordVerify: String = "",

    @field:NotBlank(message = "전회번호를 입력해주세요.")
    var phone: String = "",

    var provider: String = "",
) {
    fun toEntity(): User {
        val genderStatus: Gender
        if (gender == "MALE") {
            genderStatus = Gender.MALE
        } else {
            genderStatus = Gender.FEMALE
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

    companion object {
        fun fromEntity(user: User): UserDTO {
            return user.run {
                UserDTO(
                    email = email,
                    gender = gender.name,
                    name = name,
                    password = password?: "", // oauth 에서는 password가 없고 form login 에서는 provider 가 없음.
                    phone = phone,
                    provider = provider?: "",
                )
            }
        }
    }
}