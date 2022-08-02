package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class UserDTO (
    var email: String = "",
    var gender: String = "",
    var name: String = "",
    var password: String = "",
    var phone: String = "",
    var provider: String = "",
) {
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()

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
            email = email,
            gender = genderStatus,
            name = name,
            password = passwordEncoder.encode(password),
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