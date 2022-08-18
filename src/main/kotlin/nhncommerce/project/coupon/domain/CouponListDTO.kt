package nhncommerce.project.coupon.domain

import nhncommerce.project.baseentity.BaseEntity
import nhncommerce.project.baseentity.Status
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.*

class CouponListDTO (

    val couponId : Long = 0L,

    val userId : String,

    val status: Status,

    @field:NotBlank(message = "쿠폰명을 입력해주세요.")
    @field:Size(max = 20, message = "쿠폰명을 20자 이내로 입력하세요.")
    val couponName : String,

    @field:Min(0,message = "할인율은 0 보다 작을수 없습니다.")
    @field:Max(100,message = "할인율은 100 보다 클수 없습니다.")
    @field:NotNull(message = "할인율을 입력하세요.")
    val discountRate : Int,

    val expired : LocalDate,

    val createdAt : LocalDateTime,

    val updatedAt: LocalDateTime
)