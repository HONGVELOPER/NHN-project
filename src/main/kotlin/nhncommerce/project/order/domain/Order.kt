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
    val orderId: Long? =null,

    @Column(nullable = false)
    val price: Int,

    @Column(nullable = false)
    val phone: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    val user: User,

    @OneToOne
    @JoinColumn(name="coupon_id")
    val coupon: Coupon?= null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="option_detail_id")
    val optionDetail: OptionDetail,

    @OneToOne
    @JoinColumn(name="deliver_id")
    val deliver: Deliver,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status = Status.ACTIVE

): BaseEntity() {
    fun entityToDTO() : OrderListDTO {
        return OrderListDTO(
            orderId = orderId,
            status = status,
            price = price,
            phone = phone,
            user = user,
            coupon = coupon,
            optionDetail = optionDetail,
            deliver = deliver,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}