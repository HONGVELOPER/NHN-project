package nhncommerce.project.reviewcomment.domain

import nhncommerce.project.BaseEntity.BaseEntity
import nhncommerce.project.BaseEntity.Status
import nhncommerce.project.review.domain.Review
import javax.persistence.*

@Table(name="review_comment")
@Entity
class ReviewComment (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var reviewCommentId: Long? =null,

    @Column(nullable = false)
    var reviewComment: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="review_id")
    var review: Review,

    @Column(nullable = false)
    var status: Status = Status.ACTIVE

): BaseEntity()