package nhncommerce.project.login.domain

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

//  enum status pull request 에 올라와 있어서 주석 처리 후 재 수정함. //
//    @Column(nullable = false)
//    var status: Status,

    @Column(nullable = false)
    var gender: Gender,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, length = 11)
    var phone: Int,

//    생성일, 수정일  base entity 가져와서 하기 위해 명세 안함. //
) {
//    init {
//        this.status = Status.ACTIVE
//    }
}

//enum class Status {
//    ACTIVE,
//    IN_ACTIVE,
//}

enum class Gender {
    MALE, FEMALE
}
