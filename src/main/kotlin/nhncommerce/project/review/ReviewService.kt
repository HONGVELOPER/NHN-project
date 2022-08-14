package nhncommerce.project.review

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.image.ImageService
import nhncommerce.project.order.OrderRepository
import nhncommerce.project.order.domain.Order
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.product.domain.Product
import nhncommerce.project.review.domain.QReview
import nhncommerce.project.review.domain.Review
import nhncommerce.project.review.domain.ReviewDTO
import nhncommerce.project.review.domain.ReviewListDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.alertDTO
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.function.Function


@Service
class ReviewService(
    val reviewRepository: ReviewRepository,
    val userRepository: UserRepository,
    val orderRepository: OrderRepository,
    val productRepository: ProductRepository,
    val imageService: ImageService,
) {

    fun findReviewStatus(userId: Long, orderId: Long) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        } else if (order.reviewStatus) {
            throw RedirectException(alertDTO("이미 리뷰를 작성한 주문입니다.", "/user"))
        }
    }

    fun createReview(userId: Long, orderId: Long, reviewDTO: ReviewDTO) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        val review: Review = reviewDTO.toEntity(order)

        order.reviewStatus = true

        val reviewProduct: Product = review.product
        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(reviewProduct)
        val originTotal: Float = reviewProduct.totalStar * reviewCount
        reviewProduct.totalStar = (originTotal + review.star) / (reviewCount + 1)

        productRepository.save(reviewProduct)
        orderRepository.save(order)
        reviewRepository.save(review)
    }

    fun findReviewById(userId: Long, reviewId: Long): ReviewDTO {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
//        val product: Product = review.order.optionDetail?.product!!
        return ReviewDTO.fromEntity(review)
    }

    fun findReviewListByUser(
        userId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val user: User = userRepository.findById(userId).get()
        val booleanBuilder = reviewListBuilderByUser(PageRequestDTO(), user)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entityToDto(entity!!) }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    fun findReviewListByProduct(
        productId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val product: Product = productRepository.findById(productId).get()
        val booleanBuilder = reviewListBuilderByProduct(PageRequestDTO(), product)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entityToDto(entity!!) }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    fun updateReview(userId: Long, reviewId: Long, reviewDTO: ReviewDTO) {
        val review: Review = reviewRepository.findById(reviewId).get()
        val originStar = review.star
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        val deleteImageUrl = review.reviewImage.split("/").toList().last()
        if (review.reviewImage != "") {
            imageService.deleteImage(deleteImageUrl)
        }
        review.update(reviewDTO)

        val reviewProduct: Product = review.product
        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(reviewProduct)
        val originTotal: Float = reviewProduct.totalStar * reviewCount
        reviewProduct.totalStar = (originTotal - originStar + review.star) / reviewCount

        productRepository.save(reviewProduct)
        reviewRepository.save(review)
    }

    fun deleteReview(userId: Long, reviewId: Long) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        review.status = Status.IN_ACTIVE
        review.order.reviewStatus = false

        val reviewProduct: Product = review.product
        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(reviewProduct)

        if (reviewCount == 1) {
            reviewProduct.totalStar = 0F
        } else {
            val originTotal: Float = reviewProduct.totalStar * reviewCount
            reviewProduct.totalStar = (originTotal - review.star) / (reviewCount - 1)
        }

        productRepository.save(reviewProduct)
        reviewRepository.save(review)
        orderRepository.save(review.order)
    }

    fun entityToDto(review: Review): ReviewListDTO {
        return ReviewListDTO(
            review.reviewId,
            review.content,
            review.star,
            review.createdAt
        )
    }

    fun reviewListBuilderByUser(pageRequestDTO: PageRequestDTO, user: User): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.user.eq(user).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    fun reviewListBuilderByProduct(pageRequestDTO: PageRequestDTO, product: Product): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.product.eq(product).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }
}