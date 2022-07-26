package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import java.time.LocalDateTime
import javax.persistence.*


@Entity
class Coupon(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    val couponId : Long? = null,

    @Column(nullable = false)
    var status:Status = Status.ACTIVE,

    @Column(nullable = false)
    var couponName : String,

    @Column(nullable = false)
    var discountRate : Float,

    @Column(nullable = false)
    var expired : LocalDateTime

): BaseEntity()