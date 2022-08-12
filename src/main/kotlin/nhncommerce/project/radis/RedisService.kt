package nhncommerce.project.radis

import nhncommerce.project.radis.Constant.EventCoupon
import nhncommerce.project.radis.domain.CouponCount
import nhncommerce.project.radis.domain.TimeCoupon
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class RedisService (
    val stringRedisTemplate: StringRedisTemplate,
    val userRepository: RedisRepository,
    val redisTemplate: RedisTemplate<Any, Any>

) {
    private val log = LoggerFactory.getLogger(javaClass)
    companion object {
        var couponCount  = CouponCount(null, null, null, null, false)
    }


    //create
    fun setRedisValue(key : String, value : String){
        println("진입")
        var stringValueOperations = stringRedisTemplate.opsForValue()
        stringValueOperations.set(key, value)
        //stringValueOperations.set(key, value, LIMIT_TIME)
    }

    //read
    fun getRedisValue(key : String) : String{
        val stringValueOperations = stringRedisTemplate.opsForValue()
        val value = stringValueOperations.get(key)
        if (value == null)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        return value
    }

    //update
    fun updateRedisValue(key : String, value : String) {
        val stringValueOperations = stringRedisTemplate.opsForValue()
        stringValueOperations.getAndSet(key, value)
    }

    //delete
    fun deleteRedisValue(key :String){
        stringRedisTemplate.delete(key)
    }
    //=======================================
    //
    fun setCouponCount(eventCoupon: EventCoupon, discount : Int , queue: Int, expired : LocalDate) {
        couponCount = CouponCount(eventCoupon, discount , queue, expired, false)
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
            val timeCoupon = TimeCoupon(eventCoupon)
            log.info("'{}'님의 {} 쿠폰이 발급되었습니다 ({})", people, timeCoupon.eventCoupon.value, timeCoupon.code)
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
            val rank = redisTemplate.opsForZSet().rank(eventCoupon.value + "Queue", people!!)
            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank)
        }
    }

    //사용자 대기열 순번 조회
    fun getUserOrder(eventCoupon: EventCoupon, userId: Long) : Long? {
        return redisTemplate.opsForZSet().rank(eventCoupon.value + "Queue", userId)
    }

    //당첨되었는지 확인
    fun getEventWinCheck(eventCoupon: EventCoupon, userId : Long) : Boolean {
        val check =  redisTemplate.opsForZSet().score("Save" + eventCoupon.value, userId)
        return if (check == null) false else true
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

    //set 초기화
    fun refreshSet(eventCoupon: EventCoupon){
        redisTemplate.opsForZSet().removeRange("Save" + eventCoupon.value, 0, -1)
        redisTemplate.opsForZSet().removeRange(eventCoupon.value + "Queue", 0, -1)
    }

    //이벤트 설정 초기화
    fun resetEventSet(){
        couponCount = CouponCount(null, null, null, null, false)
    }
}