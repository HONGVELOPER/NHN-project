package nhncommerce.project.review

import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.review.domain.Review
import nhncommerce.project.review.domain.ReviewDTO
import nhncommerce.project.review.domain.ReviewListDTO

import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.validation.Valid

@Controller
class ReviewController(
    val reviewService: ReviewService,
    val loginInfoService: LoginInfoService,
) {

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

    @GetMapping("/reviews/products/{productId}")
    fun findReviewListByProduct(
        @PathVariable("productId") productId: Long,
            pageRequestDTO: PageRequestDTO,
            mav:ModelAndView
    ): ModelAndView {
        val result: PageResultDTO<ReviewListDTO, Review> =
            reviewService.findReviewListByProduct(productId, pageRequestDTO)
        mav.addObject("reviews", result)
        mav.viewName = "review/reviewListByProduct"
        return mav
    }

    @GetMapping("/api/reviews/users")
    fun findReviewListByUser(pageRequestDTO: PageRequestDTO, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val result: PageResultDTO<ReviewListDTO, Review> =
            reviewService.findReviewListByUser(loginInfo.userId, pageRequestDTO)
        mav.addObject("reviews", result)
        mav.viewName = "review/reviewListByUser"
        return mav
    }

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
        reviewService.createReview(loginInfo.userId, orderId, reviewDTO, file)
        mav.addObject("data", alertDTO("리뷰가 정상적으로 등록되었습니다.", "/api/reviews/users"))
        mav.viewName = "user/alert"
        return mav
    }

    @PutMapping("/api/reviews/{reviewId}")
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
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        reviewService.updateReview(loginInfo.userId, reviewId, reviewDTO, file)
        mav.addObject("data", alertDTO("리뷰가 정상적으로 수정되었습니다.", "/api/reviews/users"))
        mav.viewName = "user/alert"
        return mav
    }

    @DeleteMapping("/api/reviews/{reviewId}")
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

    @DeleteMapping("/api/reviews/{reviewId}/images")
    fun deleteReviewImage(@PathVariable("reviewId") reviewId: Long, redirect: RedirectAttributes): String {
        println("controller access")
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        reviewService.deleteReviewImage(loginInfo.userId, reviewId)
        redirect.addAttribute("reviewId", reviewId)
        return "redirect:/api/reviews/{reviewId}/updateForm"
    }
}