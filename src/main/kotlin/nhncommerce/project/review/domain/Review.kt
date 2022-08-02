package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.order.domain.Order
import nhncommerce.project.user.domain.User
import javax.persistence.*

@Table(name="review")
@Entity
class Review (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var reviewId: Long? =null,

    @Column(nullable = false)
    var review: String,

    @Column(nullable = false)
    var star: Float=0f,

    @Column(nullable = true)
    var reviewImageId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    var order: Order,

//    @Column(nullable = false)
//    var status: Status = Status.ACTIVE


): BaseEntity()