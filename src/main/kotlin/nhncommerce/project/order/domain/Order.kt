package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import javax.persistence.*

@Table(name="orders")
@Entity
class Order (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var orderId: Long? =null,

    @Column(nullable = false)
    var price: Int? = null,

    @Column(nullable = false)
    var phone: String? = null,

    @ManyToOne
    @JoinColumn(name="user_id")
    var userId: User? = null,

    @OneToOne
    @JoinColumn(name="coupon_id")
    var couponId: Coupon?= null,

    @ManyToOne
    @JoinColumn(name="product_id")
    var productId: Product? = null,

    //배송지 추가되면 작성
//    @OneToOne
//    @JoinColumn(name="deliver_id")
//    var deliver: Deliver,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status? = Status.ACTIVE

): BaseEntity() {
    fun toOrderDTO(): OrderDTO{
        return OrderDTO(
            orderId = orderId,
            status = status,
            price = price,
            phone = phone,
            userId = userId,
            couponId = couponId,
            productId = productId
        )
    }
}