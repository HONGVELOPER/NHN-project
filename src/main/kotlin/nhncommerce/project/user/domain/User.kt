package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    var status: Status,

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
