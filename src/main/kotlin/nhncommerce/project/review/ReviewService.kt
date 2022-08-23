package nhncommerce.project.review

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
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
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.function.Function


@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val imageService: ImageService,
    private val storageTokenService: StorageTokenService
) {

    fun findReviewStatus(userId: Long, orderId: Long) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        } else if (order.status == Status.IN_ACTIVE) {
            throw AlertException(ErrorMessage.CANCEL_ORDER)
        } else if (order.reviewStatus) {
            throw AlertException(ErrorMessage.DUPLICATE_REVIEW)
        }
    }

    @Transactional
    fun createReview(userId: Long, orderId: Long, reviewDTO: ReviewDTO, file: MultipartFile) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }

        if (!file.isEmpty) {
            generateToken()
            reviewDTO.updateReviewImage(imageService.uploadImage(file.inputStream))
        }

        val review: Review = reviewDTO.toEntity(order)
        order.updateReviewStatus(true)

        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(review.product)
        val originTotal: Float = review.product.totalStar * reviewCount

        review.product.updateTotalStar((originTotal + review.star) / (reviewCount + 1))

        productRepository.save(review.product)
        orderRepository.save(order)
        reviewRepository.save(review)
    }


    fun findReviewById(userId: Long, reviewId: Long): ReviewDTO {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
//        val product: Product = review.order.optionDetail?.product!!
        return review.entityToReviewDto()
    }

    fun findReviewListByUser(
        userId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val booleanBuilder = reviewListBuilderByUser(PageRequestDTO(), userId)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entity?.entityToReviewListDto(entity.user.email) }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    fun findReviewListByProduct(
        productId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val booleanBuilder = reviewListBuilderByProduct(PageRequestDTO(), productId)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entity?.entityToReviewListDto(entity.user.email) }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    @Transactional
    fun updateReview(userId: Long, reviewId: Long, reviewDTO: ReviewDTO, file: MultipartFile) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }

        if (!file.isEmpty) {
            generateToken()
            review.reviewImage?.let {
                imageService.deleteImage(it.split("/").toList().last())
            }
            reviewDTO.updateReviewImage(imageService.uploadImage(file.inputStream))
        }

        val originStar = review.star
        review.update(reviewDTO)

        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(review.product)
        val originTotal: Float = review.product.totalStar * reviewCount
        review.product.updateTotalStar((originTotal - originStar + review.star) / reviewCount)
    }

    @Transactional
    fun deleteReview(userId: Long, reviewId: Long) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }

        val reviewProduct: Product = review.product
        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(reviewProduct)

        val newTotalStar: Float = when(reviewCount) {
            1 -> 0F
            else -> (reviewProduct.totalStar * reviewCount - review.star) / (reviewCount - 1)
        }
        reviewProduct.updateTotalStar(newTotalStar)

        review.updateStatus(Status.IN_ACTIVE)
        review.order.updateReviewStatus(false)
    }

    fun reviewListBuilderByUser(pageRequestDTO: PageRequestDTO, userId: Long): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.user.userId.eq(userId).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    fun reviewListBuilderByProduct(pageRequestDTO: PageRequestDTO, productId: Long): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.product.productId.eq(productId).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    @Transactional
    fun deleteReviewImage(userId: Long,reviewId: Long){
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }

        review.reviewImage?.let {
            generateToken()
            imageService.deleteImage(it.split("/").toList().last())
            review.updateReviewImage(null)
        }
    }

    fun generateToken() {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)
    }

}