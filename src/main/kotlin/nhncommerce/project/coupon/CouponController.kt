package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.CouponDTO
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class CouponController(
    val couponService: CouponService
) {

    /**
     * 쿠폰 발행 페이지
     */
    @GetMapping("/publishCouponPage")
    fun createCouponPage(couponDTO: CouponDTO):String{
        return "coupon/publishCoupon"
    }

    @GetMapping("/admin")
    fun adminPage():String{
        return "admin/index"
    }

    /**
     * 쿠폰 발행
     */
    @PostMapping("/admin/coupons")
    fun createCoupon(@Valid couponDTO: CouponDTO, bindingResult: BindingResult,
                     @RequestParam("expired") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired:LocalDate,
                     session: HttpSession):String{
        println(couponDTO.toString())
        if(bindingResult.hasErrors()){
            return "coupon/publishCoupon"
        }
        couponService.createCoupon(couponDTO,expired,session)
        session.removeAttribute("isPresentUser")
        return "test"
    }

    /**
     * 쿠폰 발행 페이지에서 회원 존재 여부 판단
     */
    @PostMapping("/isPresentUser")
    fun isPresentUser(@Valid @RequestParam("email") email:String,response: HttpServletResponse, session : HttpSession):String{
        couponService.isPresentUser(email,response,session)
        return "/test"
    }

    /**
     * 쿠폰 목록 조회
     */
//    @GetMapping("/coupons}")
}