package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.CouponDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.redis.RedisService
import nhncommerce.project.redis.config.SchedulerConfiguration
import nhncommerce.project.redis.constant.EventCoupon
import nhncommerce.project.redis.domain.EventCheckDTO
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class CouponController(
    val couponService: CouponService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor,
    val schedulerConfiguration : SchedulerConfiguration,
    val redisService : RedisService,
    val loginInfoService: LoginInfoService,
) {

    @GetMapping("/api/myCouponList")
    fun myCouponList(pageRequestDTO: PageRequestDTO, model : Model) : String{
        couponService.updateCouponStatus()
        model.addAttribute("myCoupon",couponService.getMyCouponList(pageRequestDTO))
        return "coupon/myCouponList"
    }

    @GetMapping("/admin/publishCouponPage")
    fun createCouponPage(couponDTO: CouponDTO):String{
        return "coupon/publishCoupon"
    }

    @GetMapping("/admin/publishCouponListPage")
    fun getCouponListPage():String{
        return "coupon/publishCouponList"
    }

    @GetMapping("/admin/couponUpdatePage/{couponId}")
    fun couponUpdatePate(@PathVariable("couponId")couponId : Long, model: Model):String{
        val coupon = couponService.getCoupon(couponId).get()
        model.addAttribute("couponDTO", coupon)
        model.addAttribute("expired",coupon.expired)
        return "coupon/updateCoupon"
    }

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

    @GetMapping("/admin/coupons")
    fun couponList(pageRequestDTO: PageRequestDTO, model : Model) : String{
        model.addAttribute("coupons",couponService.getCouponList(pageRequestDTO))
        return "coupon/publishCouponList"
    }

    @DeleteMapping("/admin/coupons/{couponId}")
    fun removeCoupon(@PathVariable("couponId")couponId : Long) : String{
        couponService.removeCoupon(couponId)
        return "redirect:/admin/coupons"
    }

    @DeleteMapping("/coupons/{couponId}")
    fun removeMyCoupon(@PathVariable("couponId")couponId : Long) : String{
        couponService.removeCoupon(couponId)
        return "redirect:/api/myCouponList"
    }

    @PutMapping("/admin/coupons/{couponId}")
    fun updateCoupon(@PathVariable("couponId")couponId : Long, @Valid couponDTO: CouponDTO,
                        bindingResult: BindingResult, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired:LocalDate) : String{
        if(bindingResult.hasErrors()){
            return "coupon/updateCoupon"
        }
        couponService.updateCoupon(couponDTO,expired)
        return "redirect:/admin/coupons"
    }

    //쿠폰 발급 페이지
    @GetMapping("/api/coupons/event")
    fun reqCouponPage(mav : ModelAndView) : ModelAndView {
        val userId = loginInfoService.getUserIdFromSession().userId
        // 참여 여부 확인
        val checkParticipation = redisService.checkParticipation(EventCoupon.COUPON, userId)
        if (RedisService.event.progress != RedisService.EVENT_IN_PROGRESS){
            mav.addObject("data", alertDTO("진행중인 이벤트가 없습니다..", "/products"))
            mav.viewName = "redis/alert"
            return mav
        }
        if (checkParticipation){
            mav.addObject("data", alertDTO("이미 참여하였습니다.", "/products"))
            mav.viewName = "redis/alert"
            return mav
        }
        mav.addObject("userId", userId)
        mav.viewName = "redis/redis"
        return mav
    }
    //쿠폰 발급 (큐에 넣기)
    @PostMapping("/api/coupons/event")
    fun reqCoupon(model : Model, @RequestParam userId : Long) : String{
        redisService.addQueue(userId, EventCoupon.COUPON)
        return "redirect:/api/coupons/event/result"
    }

    //쿠폰 이벤트 설정 페이지
    @GetMapping("/admin/coupons/event/setting")
    fun eventCouponSetPage() : String{
        return "redis/setEvent"
    }

    //쿠폰 이벤트 설정
    @PostMapping("/admin/coupons/event/setting")
    fun eventCouponSet(@RequestParam eventCouponNum : Int, discount : Int, model: Model,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired : LocalDate) : String {
        redisService.setCouponCount(EventCoupon.COUPON, discount,eventCouponNum, expired)
        RedisService.event.progress = RedisService.SETTING_OK
        redisService.refreshSet(EventCoupon.COUPON)
        model.addAttribute("progress",  RedisService.event.progress)
        return "redis/manageEvent"
    }

    //쿠폰 이벤트 시작
    @GetMapping("/admin/coupons/event/start")
    fun eventStart(mav : ModelAndView) : ModelAndView {
        if (RedisService.event.progress == RedisService.INIT){
            mav.addObject("data", alertDTO("이벤트를 설정해주세요", "/admin/coupons/event/setting"))
            mav.viewName = "redis/alert"
            return mav
        }

        redisService.refreshSet(EventCoupon.COUPON)
        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
        RedisService.event.progress = RedisService.EVENT_IN_PROGRESS
        mav.addObject("progress", RedisService.event.progress)
        mav.viewName = "redirect:/admin/coupons/event"
        return mav
    }

    //쿠폰 이벤트 종료
    @GetMapping("/admin/coupons/event/stop")
    fun eventStop(model : Model) : String {
        postProcessor.postProcessBeforeDestruction(schedulerConfiguration, "scheduledTasks")
        RedisService.event.progress = RedisService.EVENT_END

        model.addAttribute("progress", RedisService.event.progress)
        return "redis/manageEvent"
    }

    //이벤트 쿠폰 발급
    @PostMapping("/admin/coupons/event/publish")
    fun publishCoupon(mav : ModelAndView) : ModelAndView {
        val winnerList = redisService.getWinnerList(EventCoupon.COUPON)
        if (winnerList == null || winnerList.isEmpty()){
            mav.addObject("data", alertDTO("이벤트 당첨자가 없습니다", "/admin/coupons/event"))
            mav.viewName = "redis/alert"
            return mav
        }

        for(user in winnerList){
            couponService.createEventCoupon(
                userId = user.toString().toLong(),
                discountRate = RedisService.event.discount,
                expired = RedisService.event.expired,
                couponName = EventCoupon.COUPON.value
            )
        }

        redisService.resetQueue(EventCoupon.COUPON)
        RedisService.event.progress = RedisService.PUBLISH_COUPON
        mav.viewName = "redirect:/admin/coupons"
        return mav
    }

    //이벤트 시작 종료 버튼 페이지
    @GetMapping("/admin/coupons/event")
    fun manageEvent(model : Model) : String {
        model.addAttribute("progress", RedisService.event.progress)
        return "redis/manageEvent"
    }

    //당첨자 확인 페이지
    @GetMapping("/api/coupons/event/result")
    fun checkEvent(model : Model) : String {
        val userId = loginInfoService.getUserIdFromSession().userId
        val check = redisService.getEventWinCheck(EventCoupon.COUPON, userId)
        val order = redisService.getUserOrder(EventCoupon.COUPON, userId)
        model.addAttribute("eventCheckDTO", EventCheckDTO(check, order))
        model.addAttribute("progress", RedisService.event.progress)

        return "redis/checkEventResult"
    }

}