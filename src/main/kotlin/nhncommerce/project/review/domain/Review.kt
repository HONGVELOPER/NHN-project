package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.order.domain.Order
import nhncommerce.project.user.domain.User
import javax.persistence.*

@Table(name="review")
@Entity
class Review (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val reviewId: Long? =null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var star: Int = 0,

    @Column(nullable = true)
    var reviewImage: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    val user: User,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="order_id")
//    var order: Order,

): BaseEntity() {
    fun update(reviewDTO: ReviewDTO) {
        content = reviewDTO.content
        star = reviewDTO.star
        reviewDTO.reviewImage?.let {
            reviewImage = it
        }
    }
}