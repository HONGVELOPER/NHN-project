package nhncommerce.project.redis.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import java.time.LocalDate
import javax.persistence.*

@Entity
class Event (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val eventId: Long=0L,

    @Column(nullable = false)
    val eventName : String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: Status = Status.ACTIVE,

    @Column(nullable = false)
    var discountRate : Int,

    @Column(nullable = false)
    var limits : Int = 0,

    @Column(nullable = false)
    var expired : LocalDate= LocalDate.now(),

    @Column(nullable = false)
    var progress : Int = 0,

    @Column(nullable = false)
    var eventInfo : String = "선착순 이벤트 쿠폰"
    ): BaseEntity(){

    fun changeProgress(progress : Int) {
        this.progress = progress
    }
}