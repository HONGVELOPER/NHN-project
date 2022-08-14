package nhncommerce.project.review.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import nhncommerce.project.order.domain.Order
import nhncommerce.project.product.domain.Product
import nhncommerce.project.user.domain.User
import javax.persistence.*

@Table(name="review")
@Entity
class Review(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val reviewId: Long? = null,

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
    @JoinColumn(name = "user_id")
    val user: User,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

): BaseEntity() {
    fun update(reviewDTO: ReviewDTO) {
        content = reviewDTO.content
        star = reviewDTO.star
        reviewDTO.reviewImage?.let {
            reviewImage = it
        }
    }
}