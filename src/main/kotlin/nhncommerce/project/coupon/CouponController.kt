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
    val couponService: CouponService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor,
    val schedulerConfiguration : SchedulerConfiguration,
    val redisService : RedisService,
    val loginInfoService: LoginInfoService,
    val eventRepository : EventRepository
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

    //쿠폰 발급 페이지
    @GetMapping("/api/coupons/event")
    fun reqCouponPage(mav : ModelAndView) : ModelAndView {
        val userId = loginInfoService.getUserIdFromSession().userId
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT)
        // 참여 여부 확인
        val checkParticipation = redisService.checkParticipation(event.eventName, userId)
        if (event.progress < RedisService.EVENT_IN_PROGRESS){
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
        mav.addObject("eventInfo", event.eventInfo)
        mav.viewName = "redis/redis"
        return mav
    }
    //쿠폰 발급 (큐에 넣기)
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

    //쿠폰 이벤트 설정 (ok)
    @PostMapping("/admin/coupons/event/setting")
    fun eventCouponSet(@RequestParam eventCouponNum : Int, discount : Int, eventInfo : String , model: Model,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired : LocalDate) : String {
        val event = redisService.setEvent(
                        discountRate = discount,
                        limits = eventCouponNum,
                        expired = expired,
                        progress = RedisService.SETTING_OK,
                        eventName = "TimeCoupon",
                        eventInfo=eventInfo)
        redisService.refreshSet(event.eventName)
        redisService.setNowEventId(event.eventId)
        return "redirect:/admin/coupons/event"
    }

    //쿠폰 이벤트 시작
    @GetMapping("/admin/coupons/event/start")
    fun eventStart(mav : ModelAndView) : ModelAndView {
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)
        if (event == null || event.progress == RedisService.INIT || event.progress == RedisService.PUBLISH_COUPON){
            mav.addObject("data", alertDTO("이벤트를 설정해주세요", "/admin/coupons/event/setting"))
            mav.viewName = "redis/alert"
            return mav
        }
        redisService.refreshSet("TimeCoupon")
        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
        redisService.changeProgress(event.eventId, RedisService.EVENT_IN_PROGRESS)
        mav.addObject("progress", RedisService.EVENT_IN_PROGRESS) //다시보기
        mav.viewName = "redirect:/admin/coupons/event"
        return mav
    }

    //쿠폰 이벤트 종료 (ok)
    @GetMapping("/admin/coupons/event/stop")
    fun eventStop(mav : ModelAndView) : ModelAndView {
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT_ADMIN)
        if (event.progress == RedisService.PUBLISH_COUPON){
            mav.addObject("data", alertDTO("쿠폰 발급까지 완료하였습니다. 새로운 이벤트를 등록하세요", "/admin/coupons/event/setting"))
            mav.viewName = "redis/alert"
            return mav
        }
        postProcessor.postProcessBeforeDestruction(schedulerConfiguration, "scheduledTasks")
        redisService.changeProgress(event.eventId, RedisService.EVENT_END)
        redisService.resetQueue(event.eventName)

        mav.viewName = "redirect:/admin/coupons/event"
        return mav
    }

    //이벤트 쿠폰 발급
    @PostMapping("/admin/coupons/event/publish")
    fun publishCoupon(mav : ModelAndView) : ModelAndView {
        val eventId = redisService.getNowEventId()
        val event = redisService.getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT_ADMIN)
        val winnerList = redisService.getWinnerList(event.eventName)
        if (event.progress == RedisService.PUBLISH_COUPON){
            mav.addObject("data", alertDTO("이미 발급 완료하였습니다., 새로운 이벤트를 등록하세요", "/admin/coupons/event/setting"))
            mav.viewName = "redis/alert"
            return mav
        }
        if (winnerList == null || winnerList.isEmpty()){
            redisService.changeProgress(event.eventId, RedisService.INIT)
            redisService.resetQueue(event.eventName)
            redisService.setInitCount()
            mav.addObject("data", alertDTO("이벤트 당첨자가 없습니다", "/admin/coupons/event"))
            mav.viewName = "redis/alert"
            return mav
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
        mav.viewName = "redirect:/admin/coupons"
        return mav
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
        val check = redisService.getEventWinCheck("TimeCoupon", userId)
        val order = redisService.getUserOrder("TimeCoupon", userId)
        model.addAttribute("eventCheckDTO", EventCheckDTO(check, order))
        model.addAttribute("progress", event.progress)

        return "redis/checkEventResult"
    }

}