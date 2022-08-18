package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import java.io.Serializable
import javax.persistence.*

@Table(name = "user")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val userId: Long = 0L,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column() // oauth 로그인은 비밀번호가 없음 null 허용
    var password: String? = null,

    @Column(length = 13)
    var phone: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: ROLE,

    @Column()
    val provider: String? = null, // 어디 social login 인지

    @Column()
    val oauthId: String? = null,

): BaseEntity(), Serializable { //Serializable 추가

    fun updateProfile(profileDTO: ProfileDTO) {
        name = profileDTO.name
        phone = profileDTO.phone
    }

    fun updateProfileByAdmin(adminProfileDTO: AdminProfileDTO) {
        name = adminProfileDTO.name
        phone = adminProfileDTO.phone
        role = if (adminProfileDTO.role == "ROLE_ADMIN") {
            ROLE.ROLE_ADMIN
        } else {
            ROLE.ROLE_USER
        }
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun entityToUserDto(): UserDTO {
        return UserDTO(
            email = email,
            gender = gender.name,
            name = name,
            password = password,
            phone = phone,
            provider = provider,
        )
    }

    fun entityToProfileDto(): ProfileDTO {
        return ProfileDTO(
            name = name,
            phone = phone,
        )
    }

    fun entityToAdminProfileDto(): AdminProfileDTO {
        return AdminProfileDTO(
            name = name,
            phone = phone,
            role = role.name,
        )
    }

    fun entityToUserListDto(): UserListDTO {
        return UserListDTO(
            userId = userId,
            role = role.name.split('_')[1],
            email = email,
            gender = gender.name,
            name = name,
            phone = phone,
            createdAt = createdAt
        )
    }

}

