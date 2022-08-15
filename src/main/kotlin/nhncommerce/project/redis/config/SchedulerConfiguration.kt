package nhncommerce.project.redis.config

import nhncommerce.project.redis.constant.EventCoupon
import nhncommerce.project.redis.RedisService
import nhncommerce.project.redis.RedisService.Companion.EVENT_END
import nhncommerce.project.redis.RedisService.Companion.couponCount
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
                couponCount.progress = EVENT_END //이벤트 종료 쿠폰 발급전
                log.info("==== 이벤트 종료 ====")
            } else {
                println("~~~ 이벤트 진행중 ~~~")
                redisService.publish(EventCoupon.COUPON)
                redisService.getOrder(EventCoupon.COUPON)
            }
        }
    }
}