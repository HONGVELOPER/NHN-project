package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Gender
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Table(name = "user")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val userId: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: Status,

    @Column()
    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column() // oauth 로그인은 비밀번호가 없음 null 허용
    var password: String? = null,

    @Column(nullable = false, length = 13)
    var phone: String,

    @Column()
    @Enumerated(EnumType.STRING)
    var role: ROLE = ROLE.ROLE_USER,

    @Column()
    var provider: String? = null, // 어디 social login 인지

    @Column()
    var oauthId: String? = null,

): BaseEntity() {

    fun updateProfile(profileDTO: ProfileDTO) {
        name = profileDTO.name
        phone = profileDTO.phone
    }

    fun updateProfileByAdmin(adminProfileDTO: AdminProfileDTO) {
        name = adminProfileDTO.name
        phone = adminProfileDTO.phone
        if (adminProfileDTO.role == "ROLE_ADMIN") {
            role = ROLE.ROLE_ADMIN
        } else {
            role = ROLE.ROLE_USER
        }
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }
}

