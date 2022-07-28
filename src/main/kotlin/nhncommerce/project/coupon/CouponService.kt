package nhncommerce.project.coupon

import nhncommerce.project.coupon.domain.CouponDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.AlertService
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Service
class CouponService(
    val userRepository: UserRepository,
    val alertService: AlertService,
    val couponRepository: CouponRepository
) {

    fun createCoupon(couponDTO: CouponDTO, expired: LocalDate, session: HttpSession) {
        var user = session.getAttribute("user")
        val coupon = couponDTO.toEntity(couponDTO, user as User,expired)
        couponRepository.save(coupon)
    }

    fun isPresentUser(email: String, response: HttpServletResponse, session: HttpSession) {
        val findUser = userRepository.findByEmail(email) ?: notFoundUser(response)
        session.setAttribute("email", email)
        session.setAttribute("isPresentUser", "true")
        session.setAttribute("user",findUser)
        alertService.alertMessage("존재하는 회원 입니다.","/publishCouponPage",response)
    }

    private fun notFoundUser(response: HttpServletResponse) {
        alertService.alertMessage("존재하지 않는 회원 입니다. 다시 입력해주세요.","/publishCouponPage",response)
    }
}