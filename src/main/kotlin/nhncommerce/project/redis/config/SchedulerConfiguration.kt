package nhncommerce.project.redis.config

import nhncommerce.project.redis.EventRepository
import nhncommerce.project.redis.RedisService
import nhncommerce.project.redis.RedisService.Companion.EVENT_END
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor

@Configuration
@EnableScheduling
class SchedulerConfiguration(
    val redisService: RedisService,
    val postProcessor : ScheduledAnnotationBeanPostProcessor,
    val redisTemplate: RedisTemplate<Any, Any>,
    val eventRepository: EventRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun couponEventScheduler() {
        val nowEvent = redisTemplate.opsForValue().get("nowEvent")
        if (nowEvent == null || nowEvent == 0) {
            redisService.setInitCount()
            postProcessor.postProcessBeforeDestruction(this, "scheduledTasks")
        } else if (redisService.getNowCount() == -1){
            postProcessor.postProcessBeforeDestruction(this, "scheduledTasks")
        }
        else {
            if (redisService.getNowCount() == 0){
                log.info("==== 선착순 쿠폰 끝 ====")
                val eventId = redisService.getNowEventId()
                redisService.getNowEvent(eventId)?.let {
                    it.progress = EVENT_END
                    eventRepository.save(it)
                }
                redisService.setInitCount()
                log.info("==== 이벤트 종료 ====")
                postProcessor.postProcessBeforeDestruction(this,"scheduledTasks")
            } else {
                log.info("~~~ 이벤트 진행중 ~~~")
                redisService.publish("TimeCoupon")
                redisService.getOrder("TimeCoupon")
            }
        }
    }
}