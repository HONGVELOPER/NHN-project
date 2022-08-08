package nhncommerce.project.order.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.option.domain.OptionDetail
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
    var user: User? = null,

    @OneToOne
    @JoinColumn(name="coupon_id")
    var coupon: Coupon?= null,

    @ManyToOne
    @JoinColumn(name="option_detail_id")
    var optionDetail: OptionDetail?= null,

    @OneToOne
    @JoinColumn(name="deliver_id")
    var deliver: Deliver?= null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status? = Status.ACTIVE

): BaseEntity() {
//    fun toOrderDTO(): OrderDTO{
//        return OrderDTO(
//            status = status,
//            price = price,
//            phone = phone,
//            user = user,
//            coupon = coupon,
//            optionDetail = optionDetail ,
//            deliver = deliver
//        )
//    }
}