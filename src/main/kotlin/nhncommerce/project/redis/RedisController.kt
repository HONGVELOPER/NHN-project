package nhncommerce.project.redis

import nhncommerce.project.coupon.CouponService
import nhncommerce.project.redis.RedisService.Companion.EVENT_END
import nhncommerce.project.redis.RedisService.Companion.EVENT_IN_PROGRESS
import nhncommerce.project.redis.RedisService.Companion.INIT
import nhncommerce.project.redis.RedisService.Companion.PUBLISH_COUPON
import nhncommerce.project.redis.RedisService.Companion.SETTING_OK
import nhncommerce.project.redis.constant.EventCoupon
import nhncommerce.project.redis.RedisService.Companion.event
import nhncommerce.project.redis.config.SchedulerConfiguration
import nhncommerce.project.redis.domain.EventCheckDTO
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate

@Controller
class RedisController(
    val redisService : RedisService,
    val loginInfoService: LoginInfoService,
    val couponService: CouponService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor,
    val schedulerConfiguration : SchedulerConfiguration
) {
    //쿠폰 발급 페이지
    @GetMapping("/api/eventCoupon")
    fun reqCouponPage(mav : ModelAndView) : ModelAndView {
        val userId = loginInfoService.getUserIdFromSession().userId
        // 참여 여부 확인
        val checkParticipation = redisService.checkParticipation(EventCoupon.COUPON, userId)
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
    @PostMapping("/api/eventCoupon")
    fun reqCoupon(model : Model, @RequestParam userId : Long) : String{
        redisService.addQueue(userId, EventCoupon.COUPON)
        return "redirect:/api/checkEvent"
    }

    //쿠폰 이벤트 설정 페이지
    @GetMapping("/admin/redis/eventSet")
    fun eventCouponSetPage() : String{
        return "redis/setEvent"
    }

    //쿠폰 이벤트 설정
    @PostMapping("/admin/redis/eventSet")
    fun eventCouponSet(@RequestParam eventCouponNum : Int, discount : Int, model: Model,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired : LocalDate) : String {
        redisService.setCouponCount(EventCoupon.COUPON, discount,eventCouponNum, expired)
        event.progress = SETTING_OK
        redisService.refreshSet(EventCoupon.COUPON)
        model.addAttribute("progress",  event.progress)
        return "redis/manageEvent"
    }

    //쿠폰 이벤트 시작
    @GetMapping("/admin/event/start")
    fun eventStart(mav : ModelAndView) : ModelAndView {
        //todo : 리팩토링 가능
//        if (couponCount.eventCoupon == null){
        if (event.progress == INIT){
            mav.addObject("data", alertDTO("이벤트를 설정해주세요", "/admin/redis/eventSet"))
            mav.viewName = "redis/alert"
            return mav
        }

        redisService.refreshSet(EventCoupon.COUPON)
        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
        event.progress = EVENT_IN_PROGRESS
        mav.addObject("progress", event.progress)
        mav.viewName = "redirect:/admin/eventManage"
        return mav
    }

    //쿠폰 이벤트 종료
    @GetMapping("/admin/event/stop")
    fun eventStop(model : Model) : String {
        postProcessor.postProcessBeforeDestruction(schedulerConfiguration, "scheduledTasks")
        event.progress = EVENT_END

        model.addAttribute("progress", event.progress)
        return "redis/manageEvent"
    }

    //이벤트 쿠폰 발급
    @PostMapping("/admin/event/publish")
    fun publishCoupon(mav : ModelAndView) : ModelAndView {
        val winnerList = redisService.getWinnerList(EventCoupon.COUPON)
        if (winnerList == null || winnerList.isEmpty()){
            mav.addObject("data", alertDTO("이벤트 당첨자가 없습니다", "/admin/eventManage"))
            mav.viewName = "redis/alert"
            return mav
        }

//        for(user in winnerList){
//            couponService.createEventCoupon(
//                userId = user.toString().toLong(),
//                discountRate = couponCount.discount!!,
//                expired = couponCount.expired!!,
//                couponName = EventCoupon.COUPON.value
//            )
//        }

        winnerList.let {
            couponService.createEventCoupon(
                userId = it.toString().toLong(),
                discountRate = event.discount?:0,
                expired = event.expired!!,
                couponName = EventCoupon.COUPON.value
            )
        }

        redisService.resetQueue(EventCoupon.COUPON)
        event.progress = PUBLISH_COUPON
        mav.viewName = "redirect:/admin/coupons"
        return mav
    }

    //이벤트 시작 종료 버튼 페이지
    @GetMapping("/admin/eventManage")
    fun manageEvent(model : Model) : String {
        model.addAttribute("progress", event.progress)
        return "redis/manageEvent"
    }

    //당첨자 확인 페이지
    @GetMapping("/api/checkEvent")
    fun checkEvent(model : Model) : String {
        val userId = loginInfoService.getUserIdFromSession().userId
        val check = redisService.getEventWinCheck(EventCoupon.COUPON, userId)
        //val eventNow = !redisService.validEnd()
        val order = redisService.getUserOrder(EventCoupon.COUPON, userId)
        model.addAttribute("eventCheckDTO", EventCheckDTO(check, order))
        model.addAttribute("progress", event.progress)

        return "redis/checkEventResult"
    }

}