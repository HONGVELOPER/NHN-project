package nhncommerce.project.review

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.CustomException
import nhncommerce.project.exception.ErrorCode
import nhncommerce.project.image.ImageService
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.review.domain.QReview
import nhncommerce.project.review.domain.Review
import nhncommerce.project.review.domain.ReviewDTO
import nhncommerce.project.review.domain.ReviewListDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.function.Function


@Service
class ReviewService(
    val reviewRepository: ReviewRepository,
    val userRepository: UserRepository,
//    val orderRepository: OrderRepository,
    val imageService: ImageService,
) {
    fun createReview(userId: Long, reviewDTO: ReviewDTO) {
        val user: User = userRepository.findById(userId).get()
        val review: Review = reviewDTO.toEntity(user)
        reviewRepository.save(review)
//        val order: Order =
    }

    fun findReviewById(userId: Long, reviewId: Long): ReviewDTO {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw CustomException(ErrorCode.WRONG_ACCESS)
        }
        return ReviewDTO.fromEntity(review)
    }

    fun findReviewListByUser(
        userId: Long,
        pageRequestDTO: PageRequestDTO
    ): PageResultDTO<ReviewListDTO, Review> {
        val user: User = userRepository.findById(userId).get()
        val booleanBuilder = reviewListBuilder(PageRequestDTO(), user)
        val pageable = pageRequestDTO.getPageable(Sort.by("reviewId").descending())
        val result = reviewRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Review, ReviewListDTO> =
            Function<Review, ReviewListDTO> { entity: Review? -> entityToDto(entity!!) }
        return PageResultDTO<ReviewListDTO, Review>(result, fn)
    }

    fun updateReview(userId: Long, reviewId: Long, reviewDTO: ReviewDTO) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw CustomException(ErrorCode.WRONG_ACCESS)
        }
        val deleteImageUrl = review.reviewImage.split("/").toList().last()
        if (review.reviewImage != "") {
            imageService.deleteImage(deleteImageUrl)
        }
        review.update(reviewDTO)
        reviewRepository.save(review)
    }

    fun deleteReview(userId: Long, reviewId: Long) {
        val review: Review = reviewRepository.findById(reviewId).get()
        if (review.user.userId != userId) {
            throw CustomException(ErrorCode.WRONG_ACCESS)
        }
        review.status = Status.IN_ACTIVE
        reviewRepository.save(review)
    }

    fun entityToDto(review: Review): ReviewListDTO {
        return ReviewListDTO(
            review.reviewId,
            review.content,
            review.star,
            review.createdAt
        )
    }

    fun reviewListBuilder(pageRequestDTO: PageRequestDTO, user: User): BooleanBuilder {
        val booleanBuilder: BooleanBuilder = BooleanBuilder()
        val qReview = QReview.review
        val expression = qReview.user.eq(user)
        booleanBuilder.and(expression)
        return booleanBuilder
    }
}