package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class UserDTO (
    var id: Long? = 0,
    var gender: String = "",
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var phone: Int = 0,
) {
    private val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
    fun toEntity(): User {
        val genderStatus: Gender
        if (gender == "MALE") {
            genderStatus = Gender.MALE
        } else {
            genderStatus = Gender.FEMALE
        }
        return User(
            status = Status.ACTIVE,
            gender = genderStatus,
            name = name,
            email = email,
            password = bCryptPasswordEncoder.encode(password),
            phone = phone,
            role = ROLE.ROLE_USER
        )
    }

    companion object {
        fun fromEntity(user: User): UserDTO {
            return user.run {
                UserDTO(
                    id = userId,
                    gender = gender.toString().split("_")[1],
                    name = name,
                    email = email,
                    password = password?: "",
                    phone = phone
                )
            }
        }
    }

}