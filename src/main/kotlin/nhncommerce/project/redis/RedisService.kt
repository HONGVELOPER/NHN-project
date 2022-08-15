package nhncommerce.project.redis

import nhncommerce.project.redis.constant.EventCoupon
import nhncommerce.project.redis.domain.CouponCount
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class RedisService (
    val redisTemplate: RedisTemplate<Any, Any>

) {
    private val log = LoggerFactory.getLogger(javaClass)
    companion object {
        var couponCount  = CouponCount(null, null, null, null, 0)
        const val INIT = 0
        const val SETTING_OK = 1
        const val EVENT_IN_PROGRESS = 2
        const val EVENT_END = 3
        const val PUBLISH_COUPON = 4
    }

    fun setCouponCount(eventCoupon: EventCoupon, discount : Int , queue: Int, expired : LocalDate) {
        couponCount = CouponCount(eventCoupon, discount , queue, expired, 0)
    }

    // 대기 큐에 넣기
    fun addQueue(userId : Long ,eventCoupon: EventCoupon) {
        val now = System.currentTimeMillis()
        redisTemplate.opsForZSet().add(eventCoupon.value + "Queue", userId, now.toDouble())
        log.info("대기열에 추가 - {} ({}초)", userId ,now)
    }
    // 쿠폰 발급후 큐에서 삭제
    fun publish(eventCoupon: EventCoupon) {
        val start = 0L //대기열 첫번째
        val end = 9L //대기열 마지막
        val queue = redisTemplate!!.opsForZSet().range(eventCoupon.value + "Queue", start, end)
        for (people in queue!!) {
            log.info("'{}'님의 {} 쿠폰이 발급되었습니다", people, eventCoupon.value)
            redisTemplate.opsForZSet().add( "Save" + eventCoupon.value, people, System.currentTimeMillis().toDouble())
            redisTemplate.opsForZSet().remove(eventCoupon.value + "Queue", people)
            couponCount.decrease()
        }
    }

    //대기열 조회
    fun getOrder(eventCoupon: EventCoupon) {
        val start  = 0L
        val end = -1L
        val queue = redisTemplate.opsForZSet().range(eventCoupon.value + "Queue", start, end)
        for (people in queue!!) {
            val rank = redisTemplate.opsForZSet().rank(eventCoupon.value + "Queue", people)
            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank)
        }
    }

    //사용자 대기열 순번 조회
    fun getUserOrder(eventCoupon: EventCoupon, userId: Long) : Long? {
        return redisTemplate.opsForZSet().rank(eventCoupon.value + "Queue", userId)
    }

    //당첨되었는지 확인
    fun getEventWinCheck(eventCoupon: EventCoupon, userId : Long) : Boolean {
        val check = redisTemplate.opsForZSet().score("Save" + eventCoupon.value, userId)
        return if (check == null) false else true
    }

    fun checkParticipation(eventCoupon: EventCoupon, userId : Long) : Boolean {
        val winCheck = getEventWinCheck(eventCoupon, userId)
        val order = getUserOrder(eventCoupon, userId)

        return if (winCheck == true || order != null) true else false
    }

    //당첨자 리스트 조회
    fun getWinnerList(eventCoupon: EventCoupon) : Set<Any>? {
        return redisTemplate.opsForZSet().range("Save" + eventCoupon.value, 0, -1)
    }

    //대기열에 남은 사람수
    fun getSize(eventCoupon: EventCoupon): Long {
        return redisTemplate.opsForZSet().size(eventCoupon.value + "Queue")!!
    }

    //쿠폰다 발급했는지
    fun validEnd(): Boolean {
        return if (couponCount != null) couponCount.end() else false
    }

    //대기열 초기화
    fun resetQueue(eventCoupon: EventCoupon) {
        redisTemplate.opsForZSet().removeRange(eventCoupon.value + "Queue", 0, -1)
    }

    //set 초기화
    fun refreshSet(eventCoupon: EventCoupon){
        redisTemplate.opsForZSet().removeRange("Save" + eventCoupon.value, 0, -1)
        redisTemplate.opsForZSet().removeRange(eventCoupon.value + "Queue", 0, -1)
    }

    //이벤트 설정 초기화
    fun resetEventSet(){
        couponCount = CouponCount(null, null, null, null, 0)
    }
}