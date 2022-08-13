package nhncommerce.project.redis

import nhncommerce.project.coupon.CouponService
import nhncommerce.project.redis.constant.EventCoupon
import nhncommerce.project.redis.RedisService.Companion.couponCount
import nhncommerce.project.redis.config.SchedulerConfiguration
import nhncommerce.project.redis.domain.EventCheckDTO
import nhncommerce.project.redis.domain.RedisReqDTO
import nhncommerce.project.user.UserService
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
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
    val userService: UserService,
    val loginInfoService: LoginInfoService,
    val couponService: CouponService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor,
    val schedulerConfiguration : SchedulerConfiguration
) {
    @PostMapping("/redis")
    fun create(@RequestBody reqDTO : RedisReqDTO): String {
        println("post 진입")
        redisService.setRedisValue(reqDTO.key, reqDTO.value)
        return "true"
    }

    @GetMapping("/redis")
    fun read(@RequestParam key : String) : String {
        println("hi")
        return redisService.getRedisValue(key)
    }

    @PutMapping("/redis")
    fun update(@RequestBody reqDTO: RedisReqDTO) : Boolean {
        redisService.updateRedisValue(reqDTO.key, reqDTO.value)
        return true
    }

    @DeleteMapping("/redis")
    fun delete(@RequestBody reqDTO: RedisReqDTO) : Boolean {
        redisService.deleteRedisValue(reqDTO.key)
        return true
    }
    // ================================

    //쿠폰 발급 페이지
    @GetMapping("/api/eventCoupon")
    fun reqCouponPage(model: Model) : String{
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        println("=============")
        //기존에 지원한적이 있다면 리다이렉트
        println(loginInfo.userId)
        model.addAttribute("userId", loginInfo.userId)

        return "redis/redis"
    }
    //쿠폰 발급 (큐에 넣기)
    @PostMapping("/api/eventCoupon")
    fun reqCoupon(model : Model, @RequestParam userId : Long) : String{
       //val userDTO = userService.findUserById(userId) //제거
        redisService.addQueue(userId, EventCoupon.COUPON)
        return "redirect:/api/checkEvent"
    }

    //쿠폰 이벤트 설정
    @GetMapping("/admin/redis/eventSet")
    fun eventCouponSetPage() : String{
        return "redis/setEvent"
    }

    @PostMapping("/admin/redis/eventSet")
    fun eventCouponSet(@RequestParam eventCouponNum : Int, discount : Int, model: Model,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) expired : LocalDate) : String {
        redisService.setCouponCount(EventCoupon.COUPON, discount,eventCouponNum, expired)
        model.addAttribute("setting", if (couponCount.eventCoupon == null) "No" else "Yes")
        model.addAttribute("progress",  if (couponCount.progress!!) "Yes" else "No")
        return "redis/manageEvent"
    }
    //쿠폰 이벤트 시작
//    @GetMapping("/admin/event/start")
//    fun eventStart() : String {
//        redisService.refreshSet(EventCoupon.COUPON)
//        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
//        return "redis/manageEvent"
//    }
    @GetMapping("/admin/event/start")
    fun eventStart(mav : ModelAndView) : ModelAndView {
        if (couponCount.eventCoupon == null){
            mav.addObject("data", alertDTO("이벤트를 설정해주세요", "/admin/redis/eventSet"))
            mav.viewName = "redis/alert"
            return mav
        }

        redisService.refreshSet(EventCoupon.COUPON)
        postProcessor.postProcessAfterInitialization(schedulerConfiguration, "scheduledTasks")
        couponCount.progress = true
        mav.addObject("setting", if (couponCount.eventCoupon == null) "No" else "Yes")
        mav.addObject("progress", if (couponCount.progress!!) "Yes" else "No")
        mav.viewName = "redis/manageEvent"
        return mav
    }

    //쿠폰 이벤트 종료
    @GetMapping("/admin/event/stop")
    fun eventStop(model : Model) : String {
        postProcessor.postProcessBeforeDestruction(schedulerConfiguration, "scheduledTasks")
        couponCount.eventCoupon = null
        couponCount.progress = false
        model.addAttribute("setting", if (couponCount.eventCoupon == null) "No" else "Yes")
        model.addAttribute("progress",if (couponCount.progress!!) "Yes" else "No")
        return "redis/manageEvent"
    }

    //이벤트 쿠폰 발급
    @PostMapping("/admin/event/publish")
    fun publishCoupon(mav : ModelAndView) : ModelAndView {
        val winnerList = redisService.getWinnerList(EventCoupon.COUPON)
        if (winnerList?.size == 0 || winnerList == null){
            mav.addObject("data", alertDTO("이벤트 당첨자가 없습니다", "/admin/eventManage"))
            mav.viewName = "redis/alert"
            return mav
        }
        //주고나서 캐시 비우기 추가
        //수정 필요
        for(user in winnerList!!){
            couponService.createEventCoupon(
                userId = user.toString().toLong(),
                discountRate = couponCount.discount!!,
                expired = couponCount.expired!!,
                couponName = EventCoupon.COUPON.value
            )
        }
        redisService.refreshSet(EventCoupon.COUPON)
        mav.viewName = "redirect:/admin/coupons"
        return mav
    }

    //이벤트 시작 종료 버튼 페이지
    @GetMapping("/admin/eventManage")
    fun manageEvent(model : Model) : String {
        model.addAttribute("setting", if (couponCount.eventCoupon == null) "No" else "Yes")
        model.addAttribute("progress", if (couponCount.progress!!) "Yes" else "No")
        return "redis/manageEvent"
    }

    //당첨자 확인 페이지
    @GetMapping("/api/checkEvent")
    fun checkEvent(model : Model) : String {
        val userId = loginInfoService.getUserIdFromSession().userId
        val check = redisService.getEventWinCheck(EventCoupon.COUPON, userId)
        val eventNow = !redisService.validEnd()
        val order = redisService.getUserOrder(EventCoupon.COUPON, userId)
        model.addAttribute("eventCheckDTO", EventCheckDTO(check, order, eventNow))

        return "redis/checkEventResult"
    }

}