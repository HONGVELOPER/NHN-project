package nhncommerce.project.review

import nhncommerce.project.image.ImageService
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.review.domain.Review
import nhncommerce.project.review.domain.ReviewDTO
import nhncommerce.project.review.domain.ReviewListDTO
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import javax.validation.Valid

@Controller
class ReviewController(
    val reviewService: ReviewService,
    val loginInfoService: LoginInfoService,
    val storageTokenService: StorageTokenService,
    val imageService: ImageService,
) {

    /*
    * 리뷰 작성 페이지 + 주문 완성되면 어떤 주문에 대하여 리뷰 작성할건지 파라미터 넘기고 리뷰 작성 유무 판별헤야함.
    * */
    @GetMapping("/api/reviews/orders/{orderId}/createForm")
    fun reviewCreateForm(
        @PathVariable orderId: Long,
        reviewDTO: ReviewDTO,
        mav: ModelAndView
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        reviewService.findReviewStatus(loginInfo.userId, orderId)
        mav.addObject("orderId", orderId)
        mav.viewName = "review/create"
        return mav
    }

    /*
    * 리뷰 수정 페이지
    * */
    @GetMapping("/api/reviews/{reviewId}/updateForm")
    fun reviewUpdateForm(
        @PathVariable("reviewId") reviewId: Long,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val reviewDTO: ReviewDTO = reviewService.findReviewById(loginInfo.userId, reviewId)
        mav.addObject("reviewId", reviewId)
        mav.addObject("reviewDTO", reviewDTO)
        mav.viewName = "review/update"
        return mav
    }

    /*
    * 리뷰 페이지 - 상품 기준
    * */
    @GetMapping("/reviews/products/{productId}")
    fun reviewListByProductId(
        @PathVariable("productId") productId: Long,
            pageRequestDTO: PageRequestDTO,
            mav:ModelAndView
    ):ModelAndView {
        val result: PageResultDTO<ReviewListDTO, Review> =
            reviewService.findReviewListByProduct(productId, pageRequestDTO)
        mav.addObject("reviews", result)
        mav.viewName = "review/reviewListByProduct"
        return mav
    }

    /*
    * 리뷰 페이지 - 유저 기준
    * */
    @GetMapping("/api/reviews/users")
    fun findReviewByUser(pageRequestDTO: PageRequestDTO, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val result: PageResultDTO<ReviewListDTO, Review> =
            reviewService.findReviewListByUser(loginInfo.userId, pageRequestDTO)
        mav.addObject("reviews", result)
        mav.viewName = "review/reviewListByUser"
        return mav
    }

    /*
    * 리뷰 작성
    * */
//    @GetMapping("/users/review/list")
    @PostMapping("/api/reviews/orders/{orderId}")
    fun createReview(
        @PathVariable("orderId") orderId: Long,
        @Valid @ModelAttribute reviewDTO: ReviewDTO,
        bindingResult: BindingResult,
        @RequestPart file : MultipartFile,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "review/create"
            return mav
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (!file.isEmpty) {
            val getToken = storageTokenService.getTokenId()
            imageService.insertTokenId(getToken)
            val reviewImageUrl = imageService.uploadImage(file.inputStream)
            reviewDTO.reviewImage = reviewImageUrl
        }
        reviewService.createReview(loginInfo.userId, orderId, reviewDTO)
        mav.addObject("data", alertDTO("리뷰가 정상적으로 등록되었습니다.", "/api/reviews/users"))
        mav.viewName = "user/alert"
        return mav
    }

    @PutMapping("/api/reviews/{reviewId}/update")
    fun updateReview(
        @PathVariable("reviewId") reviewId: Long,
        @Valid @ModelAttribute reviewDTO: ReviewDTO,
        bindingResult: BindingResult,
        @RequestPart file : MultipartFile,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "review/update"
            return mav
        }
        if (!file.isEmpty) {
            val getToken = storageTokenService.getTokenId()
            imageService.insertTokenId(getToken)
            val reviewImageUrl = imageService.uploadImage(file.inputStream)
            reviewDTO.reviewImage = reviewImageUrl
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        reviewService.updateReview(loginInfo.userId, reviewId, reviewDTO)
        mav.addObject("data", alertDTO("리뷰가 정상적으로 수정되었습니다.", "/api/reviews/users"))
        mav.viewName = "user/alert"
        return mav
    }

    @PutMapping("/api/reviews/{reviewId}/delete")
    fun deleteReview(
        @PathVariable("reviewId") reviewId: Long,
        mav: ModelAndView
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        reviewService.deleteReview(loginInfo.userId, reviewId)
        mav.addObject("data", alertDTO("리뷰가 정상적으로 삭제되었습니다.", "/api/reviews/users"))
        mav.viewName = "user/alert"
        return mav
    }
}