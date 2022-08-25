package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.CouponDTO
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.redis.EventRepository
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
    private val couponService: CouponService,
    private val postProcessor : ScheduledAnnotationBeanPostProcessor,
    private val schedulerConfiguration : SchedulerConfiguration,
    private val redisService : RedisService,
    private val loginInfoService: LoginInfoService,
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
        model.addAttribute("type", pageRequestDTO.type)
        model.addAttribute("keyword", pageRequestDTO.keyword)
        return "coupon/publishCouponList"
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

    //쿠폰 이벤트 참여 페이지
    @GetMapping("/api/coupons/event")
    fun reqCouponPage(model : Model) : String {
        val userId = loginInfoService.getUserIdFromSession().userId
        val event = redisService.getEventAtApply(userId)

        model.addAttribute("userId", userId)
        model.addAttribute("eventInfo", event.eventInfo)
        return "redis/redis"
    }
    //쿠폰 이벤트 참여 (큐에 넣기)
    @PostMapping("/api/coupons/event")
    fun reqCoupon(model : Model, @RequestParam userId : Long) : String{
        redisService.addQueue(userId, "TimeCoupon")
        return "redirect:/api/coupons/event/result"
    }

    //쿠폰 이벤트 설정 페이지
    @GetMapping("/admin/coupons/event/setting")
    fun eventCouponSetPage() : String{
        return "redis/setEvent"
    }

    //쿠폰 이벤트 설정
    @PostMapping("/admin/coupons/event/setting")
    fun eventCouponSet(@RequestParam eventCouponNum : Int, discount : Int, eventInfo : String , model: Model,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired : LocalDate) : String {
        val event = redisService.setEvent(
                        discountRate = discount,
                        limits = eventCouponNum,
                        expired = expired,
                        progress = RedisService.SETTING_OK,
                        eventName = "TimeCoupon",
                        eventInfo = eventInfo)

        redisService.refreshSet(event.eventName)
        redisService.setNowEventId(event.eventId)
        return "redirect:/admin/coupons/event"
    }

    //쿠폰 이벤트 시작
    @GetMapping("/admin/coupons/event/start")
    fun eventStart(model : Model) : String {
        val event = redisService.getEventAtStart()

        redisService.refreshSet("TimeCoupon")
        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
        redisService.changeProgress(event.eventId, RedisService.EVENT_IN_PROGRESS)

        model.addAttribute("progress", event.progress)
        return "redirect:/admin/coupons/event"
    }

    //쿠폰 이벤트 종료
    @GetMapping("/admin/coupons/event/stop")
    fun eventStop(model : Model) : String {
        val event = redisService.getEventAtEnd()

        postProcessor.postProcessBeforeDestruction(schedulerConfiguration, "scheduledTasks")
        redisService.changeProgress(event.eventId, RedisService.EVENT_END)
        redisService.resetQueue(event.eventName)

        return "redirect:/admin/coupons/event"
    }

    //이벤트 쿠폰 발급
    @PostMapping("/admin/coupons/event/publish")
    fun publishCoupon(model : Model) : String {
        val event = redisService.getEventAtPublish()
        val winnerList = redisService.getWinnerList(event.eventName)

        if (winnerList == null || winnerList.isEmpty()){
            redisService.changeProgress(event.eventId, RedisService.INIT)
            redisService.resetQueue(event.eventName)
            redisService.setInitCount()
            throw AlertException(ErrorMessage.EMPTY_WINNER_LIST)
        }

        for(user in winnerList){
            couponService.createEventCoupon(
                userId = user.toString().toLong(),
                discountRate = event.discountRate,
                expired = event.expired,
                couponName = event.eventName
            )
        }
        redisService.resetQueue(event.eventName)
        redisService.setInitCount()
        redisService.changeProgress(event.eventId, RedisService.PUBLISH_COUPON)

        return "redirect:/admin/coupons"
    }

    //이벤트 시작 종료 버튼 페이지
    @GetMapping("/admin/coupons/event")
    fun manageEvent(model : Model) : String {
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)

        model.addAttribute("eventInfo", event?.eventInfo ?: "이벤트 정보가 없습니다.")
        model.addAttribute("progress", event?.progress ?: RedisService.INIT)
        return "redis/manageEvent"
    }

    //당첨자 확인 페이지
    //todo :리팩토링
    @GetMapping("/api/coupons/event/result")
    fun checkEvent(model : Model) : String {
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT)
        val userId = loginInfoService.getUserIdFromSession().userId
        val check = redisService.getEventWinCheck(event.eventName, userId)
        val order = redisService.getUserOrder(event.eventName, userId)

        model.addAttribute("eventCheckDTO", EventCheckDTO(check, order))
        model.addAttribute("progress", event.progress)

        return "redis/checkEventResult"
    }

}