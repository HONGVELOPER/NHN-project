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

    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column() // oauth 로그인은 비밀번호가 없음 null 허용
    var password: String? = null,

    @Column(length = 11)
    var phone: Int,

    @Column()
    @Enumerated(EnumType.STRING)
    var role: ROLE = ROLE.ROLE_USER,

    @Column()
    var provider: String? = null, // 어디 social login 인지

    @Column()
    var oauthId: String? = null, //

    ): BaseEntity() {

//    init {
//        this.status = Status.ACTIVE
//    }

    // status 부분도 업데이트 할 수 있게 -> DTO 도 수정해야해
    fun update(userDTO: UserDTO) {
//        status = userDTO.st
        name = userDTO.name
        email = userDTO.email
        password = userDTO.password
        phone = userDTO.phone
    }
}