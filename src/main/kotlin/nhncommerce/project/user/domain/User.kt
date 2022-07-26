package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user")
@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val userId: Long? = null,   

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

): BaseEntity() {
    init {
        this.status = Status.ACTIVE
    }
}

enum class Gender {
    MALE, FEMALE
}
