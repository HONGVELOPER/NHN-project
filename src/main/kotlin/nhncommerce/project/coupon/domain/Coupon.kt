package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*


@Entity
class Coupon(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val couponId : Long? = null,

    @ManyToOne
    @JoinColumn(name = "userId")
    val userID : User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status:Status = Status.ACTIVE,

    @Column(nullable = false)
    var couponName : String,

    @Column(nullable = false)
    var discountRate : Int,

    @Column(nullable = false)
    var expired : LocalDate

): BaseEntity()