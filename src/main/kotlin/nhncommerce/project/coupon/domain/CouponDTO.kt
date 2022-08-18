package nhncommerce.project.coupon.domain


import nhncommerce.project.baseentity.Status
import nhncommerce.project.user.domain.User
import java.time.LocalDate
import javax.validation.constraints.*

data class CouponDTO (

    val couponId : Long = 0L,

    var status: Status = Status.ACTIVE,

    @field:NotBlank(message = "쿠폰명을 입력해주세요.")
    @field:Size(max = 20, message = "쿠폰명을 20자 이내로 입력하세요.")
    var couponName: String="",

    @field:Min(0,message = "할인율은 0 보다 작을수 없습니다.")
    @field:Max(100,message = "할인율은 100 보다 클수 없습니다.")
    @field:NotNull(message = "할인율을 입력하세요.")
    var discountRate: Int=0,

){

    fun dtoToEntity(user: User, expired: LocalDate): Coupon {
        return Coupon(couponId, user, status, couponName, discountRate, expired)
    }

}