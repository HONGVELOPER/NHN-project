package nhncommerce.project.coupon.domin

import nhncommerce.project.BaseEntity.BaseEntity
import nhncommerce.project.BaseEntity.Status
import java.time.LocalDateTime
import javax.persistence.*

@Entity
open class Coupon(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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