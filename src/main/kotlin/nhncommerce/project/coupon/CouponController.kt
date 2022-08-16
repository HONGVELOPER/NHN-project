package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.CouponDTO
import nhncommerce.project.page.PageRequestDTO
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class CouponController(
    val couponService: CouponService
) {

    /**
     * 나의 쿠폰 리스트
     */
    @GetMapping("/api/myCouponList")
    fun myCouponList(pageRequestDTO: PageRequestDTO, model : Model) : String{
        couponService.updateCouponStatus()
        model.addAttribute("myCoupon",couponService.getMyCouponList(pageRequestDTO))
        return "coupon/myCouponList"
    }

    /**
     * 쿠폰 발행 페이지
     */
    @GetMapping("/admin/publishCouponPage")
    fun createCouponPage(couponDTO: CouponDTO):String{
        return "coupon/publishCoupon"
    }

    /**
     * 쿠폰 발행 리스트 페이지
     */
    @GetMapping("/admin/publishCouponListPage")
    fun getCouponListPage():String{
        return "coupon/publishCouponList"
    }

    /**
     * 쿠폰 수정 페이지
     */
    @GetMapping("/admin/couponUpdatePage/{couponId}")
    fun couponUpdatePate(@PathVariable("couponId")couponId : Long, model: Model):String{
        val coupon = couponService.getCoupon(couponId).get()
        model.addAttribute("couponDTO", coupon)
        model.addAttribute("expired",coupon.expired)
        return "coupon/updateCoupon"
    }

    /**
     * 쿠폰 발행
     */
    @PostMapping("/admin/coupons")
    fun createCoupon(@Valid couponDTO: CouponDTO, bindingResult: BindingResult,
                     @RequestParam("expired") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired:LocalDate
                    ,@RequestParam("email") email:String):String{
        if(bindingResult.hasErrors()){
            return "coupon/publishCoupon"
        }
        couponService.createCoupon(couponDTO,expired,email)
        return "redirect:/admin/coupons"
    }

    /**
     * 쿠폰 목록 조회
     */
    @GetMapping("/admin/coupons")
    fun couponList(pageRequestDTO: PageRequestDTO, model : Model) : String{
        model.addAttribute("coupons",couponService.getCouponList(pageRequestDTO))
        return "coupon/publishCouponList"
    }

    /**
     * 쿠폰 삭제
     */
    @DeleteMapping("/admin/coupons/{couponId}")
    fun removeCoupon(@PathVariable("couponId")couponId : Long) : String{
        couponService.removeCoupon(couponId)
        return "redirect:/admin/coupons"
    }

    /**
     * 사용자 나의 쿠폰 삭제
     */
    @DeleteMapping("/coupons/{couponId}")
    fun removeMyCoupon(@PathVariable("couponId")couponId : Long) : String{
        couponService.removeCoupon(couponId)
        return "redirect:/api/myCouponList"
    }

    /**
     * 쿠폰 수정
     */
    @PutMapping("/admin/coupons/{couponId}")
    fun updateCoupon(@PathVariable("couponId")couponId : Long, @Valid couponDTO: CouponDTO,
                        bindingResult: BindingResult, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired:LocalDate) : String{
        if(bindingResult.hasErrors()){
            return "coupon/updateCoupon"
        }
        couponService.updateCoupon(couponDTO,expired)
        return "redirect:/admin/coupons"
    }

}