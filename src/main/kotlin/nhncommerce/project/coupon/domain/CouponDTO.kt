package nhncommerce.project.coupon.domain


import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class CouponDTO (

    val couponId : Long?=null,

    var status: Status = Status.ACTIVE,

    @field:NotBlank(message = "쿠폰명을 입력해주세요.")
    @field:Size(max = 20, message = "쿠폰명을 20자 이내로 입력하세요.")
    var couponName: String="",

    @field:Min(0,message = "할인율은 0 보다 작을수 없습니다.")
    @field:Max(100,message = "할인율은 100 보다 클수 없습니다.")
    @field:NotNull(message = "할인율을 입력하세요.")
    var discountRate: Int=0,

)