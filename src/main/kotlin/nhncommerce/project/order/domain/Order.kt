package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.coupon.domin.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.product.domin.Product
import java.time.LocalDateTime
import javax.persistence.*

@Table(name="orders")
@Entity
class Order (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var orderId: Long? =null,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var phone: String,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="user_id")
//    var user: User,

    @OneToOne
    @JoinColumn(name="coupon_id")
    var coupon: Coupon,

    @ManyToOne(fetch = FetchType.LAZY)
    var product: Product,

//    @OneToOne
//    @JoinColumn(name="deliver_id")
//    var deliver: Deliver,

//    @Column(nullable = false)
//    var status: Status = Status.ACTIVE

): BaseEntity()