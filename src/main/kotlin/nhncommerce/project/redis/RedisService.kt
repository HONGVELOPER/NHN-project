package nhncommerce.project.redis

import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.redis.domain.Event
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RedisService (
    val redisTemplate: RedisTemplate<Any, Any>,
    val eventRepository: EventRepository

) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun setEvent(eventName : String, discountRate : Int , limits : Int, expired : LocalDate, progress : Int, eventInfo : String) : Event {
        val event = Event(
           eventName = eventName,
           discountRate = discountRate,
           limits = limits,
           expired = expired,
           progress =  progress,
            eventInfo = eventInfo
        )
        redisTemplate.opsForValue().set("count", limits)
        return eventRepository.save(event)
    }

    fun getNowCount() : Int{
        return redisTemplate.opsForValue().get("count").toString().toInt()
    }

    // 대기 큐에 넣기
    fun addQueue(userId : Long ,eventName: String) {
        val now = System.currentTimeMillis()
        redisTemplate.opsForZSet().add("${eventName}Queue", userId, now.toDouble())
        log.info("대기열에 추가 - ${userId} (${now}초)")
    }
    // 쿠폰 발급후 큐에서 삭제
    fun publish(eventName: String) {
        val start = 0L //대기열 첫번째
        var end = 9L //대기열 마지막
        val nowCount = getNowCount().toLong()
        if (nowCount < 10)
            end = nowCount - 1L
        val queue = redisTemplate.opsForZSet().range("${eventName}Queue", start, end)

        queue?.let {
            for (userId in it) {
                log.info("${userId}님의 ${eventName} 쿠폰이 발급되었습니다")
                redisTemplate.opsForZSet().add("Save$eventName", userId, System.currentTimeMillis().toDouble())
                redisTemplate.opsForZSet().remove("${eventName}Queue", userId)
                redisTemplate.opsForValue().decrement("count")
            }
        }
    }

    //대기열 조회
    fun getOrder(eventName: String) {
        val start  = 0L
        val end = -1L
        val queue = redisTemplate.opsForZSet().range("${eventName}Queue", start, end)

        queue?.let {
            for (userId in it) {
                val rank = redisTemplate.opsForZSet().rank("${eventName}Queue", userId)
                log.info("${userId}님의 현재 대기열은 ${rank}명 남았습니다.")
            }
        }
    }

    //사용자 대기열 순번 조회
    fun getUserOrder(eventName: String, userId: Long) : Long? {
        return redisTemplate.opsForZSet().rank(eventName + "Queue", userId)
    }

    //당첨되었는지 확인
    fun getEventWinCheck(eventName: String, userId : Long) : Boolean {
        val check = redisTemplate.opsForZSet().score("Save$eventName", userId)
        return check != null
    }

    fun checkParticipation(eventName: String, userId : Long) : Boolean {
        val winCheck = getEventWinCheck(eventName, userId)
        val order = getUserOrder(eventName, userId)

        return winCheck || order != null
    }

    //당첨자 리스트 조회
    fun getWinnerList(eventName: String) : Set<Any>? {
        return redisTemplate.opsForZSet().range("Save$eventName", 0, -1)
    }

    //대기열에 남은 사람수
    fun getSize(eventName: String): Long {
        return redisTemplate.opsForZSet().size("${eventName}Queue")!!
    }

    //대기열 초기화
    fun resetQueue(eventName: String) {
        redisTemplate.opsForZSet().removeRange("${eventName}Queue", 0, -1)
    }

    //set 초기화
    fun refreshSet(eventName: String){
        redisTemplate.opsForZSet().removeRange("Save$eventName", 0, -1)
        redisTemplate.opsForZSet().removeRange("${eventName}Queue", 0, -1)
    }

    @Transactional
    fun changeProgress(eventId : Long, progress : Int){
        val event = eventRepository.findById(eventId).get()
        event.changeProgress(progress)
    }

    fun setNowEventId(eventId : Long){
        redisTemplate.opsForValue().set("nowEvent", eventId)
    }

    fun setInitCount(){
        redisTemplate.opsForValue().set("count", -1)
    }

    fun getNowEventId() : Long {
        val eventId = redisTemplate.opsForValue().get("nowEvent") ?: 0
        return eventId.toString().toLong()
    }

    fun getNowEvent(eventId : Long) : Event? {
        return try{
            eventRepository.findById(eventId).get()
        } catch (e: Exception){
            null
        }
    }

    //쿠폰 발급시 예외처리후 이벤트 조회
    fun getEventAtApply(userId : Long) : Event{
        val eventId = getNowEventId()
        val event = getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT)

        if (event.progress < EVENT_IN_PROGRESS)
            throw AlertException(ErrorMessage.EMPTY_NOW_EVENT)
        if (checkParticipation(event.eventName, userId))
            throw AlertException(ErrorMessage.DUPLICATE_PARTICIPATION)
        return event
    }

    //쿠폰 이벤트 시작시 예외처리후 이벤트 조회
    fun getEventAtStart() : Event {
        val eventId = getNowEventId()
        val event = getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_EVENT_SETTING)

        if (event.progress == INIT || event.progress == PUBLISH_COUPON)
            throw AlertException(ErrorMessage.EMPTY_EVENT_SETTING)
        if (event.progress == EVENT_END)
            throw AlertException(ErrorMessage.ALREADY_END_EVENT)
        if (event.progress == EVENT_IN_PROGRESS)
            throw AlertException(ErrorMessage.EVENT_IN_PROGRESS)
        return event
    }

    //쿠폰 이벤트 종료시 예외처리후 이벤트 조회
    fun getEventAtEnd() : Event {
        val eventId = getNowEventId()
        val event = getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT_ADMIN)
        if (event.progress == PUBLISH_COUPON)
            throw AlertException(ErrorMessage.ALREADY_PUBLISH_COUPON)
        if (event.progress != EVENT_IN_PROGRESS)
            throw AlertException(ErrorMessage.EVENT_NOT_IN_PROGRESS_ADMIN)
        return event
    }

    fun getEventAtPublish() :Event {
        val eventId = getNowEventId()
        val event = getNowEvent(eventId)?: throw AlertException(ErrorMessage.EMPTY_NOW_EVENT_ADMIN)
        if (event.progress < EVENT_END)
            throw AlertException(ErrorMessage.NOT_END_EVENT)
        if (event.progress == PUBLISH_COUPON)
            throw AlertException(ErrorMessage.ALREADY_PUBLISH_COUPON)
        return event
    }

    companion object {
        const val INIT = 0
        const val SETTING_OK = 1
        const val EVENT_IN_PROGRESS = 2
        const val EVENT_END = 3
        const val PUBLISH_COUPON = 4
    }
}