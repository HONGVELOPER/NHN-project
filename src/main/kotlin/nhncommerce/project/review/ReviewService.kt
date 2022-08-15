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
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.function.Function


@Service
class ReviewService(
    val reviewRepository: ReviewRepository,
    val userRepository: UserRepository,
    val orderRepository: OrderRepository,
    val productRepository: ProductRepository,
    val imageService: ImageService,
    val storageTokenService: StorageTokenService
) {

    /*
    * 리뷰 상태 확인 (이미 작성한 리뷰인지)
    * */
    fun findReviewStatus(userId: Long, orderId: Long) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        } else if (order.status == Status.IN_ACTIVE) {
            throw RedirectException(alertDTO("취소된 주문입니다.", "/user"))
        } else if (order.reviewStatus) {
            throw RedirectException(alertDTO("이미 리뷰를 작성한 주문입니다.", "/user"))
        }
    }

    /*
    * 리뷰 생성
    * */
    fun createReview(userId: Long, orderId: Long, reviewDTO: ReviewDTO, file: MultipartFile) {
        val order: Order = orderRepository.findById(orderId).get()
        if (order.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }

        if (!file.isEmpty) {
            generateToken()
            val reviewImageUrl = imageService.uploadImage(file.inputStream)
            reviewDTO.reviewImage = reviewImageUrl
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


    /*
    * 리뷰 단일 조회
    * */
    fun findReviewById(userId: Long, reviewId: Long): ReviewDTO {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
//        val product: Product = review.order.optionDetail?.product!!
        return review.entityToReviewDto()
    }

    /*
    * 리뷰 목록 조회 - 유저 기준
    * */
    fun findReviewListByUser(
        userId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val user: User = userRepository.findById(userId).get()
        val booleanBuilder = reviewListBuilderByUser(PageRequestDTO(), user)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entity?.entityToReviewListDto() }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    /*
    * 리쥬 목록 조회 - 상품 기준
    * */
    fun findReviewListByProduct(
        productId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val product: Product = productRepository.findById(productId).get()
        val booleanBuilder = reviewListBuilderByProduct(PageRequestDTO(), product)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entity?.entityToReviewListDto() }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    /*
    * 리뷰 수정
    * */
    fun updateReview(userId: Long, reviewId: Long, reviewDTO: ReviewDTO, file: MultipartFile) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }

        if (!file.isEmpty) {
            generateToken()
            review.reviewImage?.let {
                val deleteImageUrl = it.split("/").toList().last()
                imageService.deleteImage(deleteImageUrl)
            }
            val reviewImageUrl = imageService.uploadImage(file.inputStream)
            reviewDTO.reviewImage = reviewImageUrl
        }

        val originStar = review.star
        review.update(reviewDTO)

        val reviewProduct: Product = review.product
        val reviewCount: Int = reviewRepository.findActiveReviewCountByProduct(reviewProduct)
        val originTotal: Float = reviewProduct.totalStar * reviewCount
        reviewProduct.totalStar = (originTotal - originStar + review.star) / reviewCount

        productRepository.save(reviewProduct)
        reviewRepository.save(review)
    }

    /*
    * 리뷰 삭제
    * */
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

    /*
    * 리뷰 목록 조회 조건 - 유저 기준
    * */
    fun reviewListBuilderByUser(pageRequestDTO: PageRequestDTO, user: User): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.user.eq(user).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    /*
    * 리뷰 목록 조회 조건 - 상품 기준
    * */
    fun reviewListBuilderByProduct(pageRequestDTO: PageRequestDTO, product: Product): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.product.eq(product).and(qReview.status.eq(Status.ACTIVE))
        booleanBuilder.and(expression)
        return booleanBuilder
    }

    /*
    * 리뷰 이미지 삭제 - 업데이트 과정
    * */
    fun deleteReviewImage(userId: Long,reviewId: Long){
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw RedirectException(alertDTO("잘못된 접근입니다.", "/user"))
        }
        review.reviewImage?.let {
            generateToken()
            val deleteImageUrl = it.split("/").toList().last()
            imageService.deleteImage(deleteImageUrl)
            review.reviewImage = null
            reviewRepository.save(review)
        }
    }

    fun generateToken() {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)
    }

}