package nhncommerce.project.radis.config

import nhncommerce.project.radis.Constant.EventCoupon
import nhncommerce.project.radis.RedisController
import nhncommerce.project.radis.RedisService
import nhncommerce.project.radis.RedisService.Companion.couponCount
import nhncommerce.project.radis.domain.CouponCount
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor

@Configuration
@EnableScheduling
class SchedulerConfiguration(
    val redisService: RedisService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun couponEventScheduler() {
        if (couponCount.eventCoupon == null)
            postProcessor.postProcessBeforeDestruction(this,"scheduledTasks")
        else {
            if (redisService.validEnd()){
                log.info("==== 선착순 쿠폰 끝 ====")
                postProcessor.postProcessBeforeDestruction(this,"scheduledTasks")
                couponCount.eventCoupon = null
                couponCount.progress = false
                log.info("==== 이벤트 종료 ====")
            } else {
                println("^^^^^")
                redisService.publish(EventCoupon.COUPON)
                redisService.getOrder(EventCoupon.COUPON)
            }
        }
    }
}