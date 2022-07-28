package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.*

@Table(name = "user")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val userId: Long? = null,

    @Column(nullable = false)
    var status: Status,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gender: Gender,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, length = 11)
    var phone: Int,

//    생성일, 수정일  base entity 가져와서 하기 위해 명세 안함. //
): BaseEntity() {
    init {
        this.status = Status.ACTIVE
    }
}

enum class Gender {
    MALE, FEMALE
}
