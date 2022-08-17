package nhncommerce.project.coupon


import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.domain.*
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Service
class CouponService(
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val loginInfoService: LoginInfoService
) {

    fun createCoupon(couponDTO: CouponDTO, expired: LocalDate, session: HttpSession) {
        val user = session.getAttribute("user")
        val coupon = couponDTO.dtoToEntity(couponDTO, user as User,expired)
        couponRepository.save(coupon)
    }

    //이벤트 쿠폰 발급
    fun createEventCoupon(userId : Long, discountRate : Int,  expired: LocalDate, couponName : String){
        val user = userRepository.findById(userId).get()
        val coupon = Coupon(null, user, Status.ACTIVE, couponName, discountRate, expired)
        couponRepository.save(coupon)
    }

    fun isPresentUser(email: String, response: HttpServletResponse, session: HttpSession) {
        val findUser = userRepository.findByEmail(email) ?: notFoundUser(response,session)
        session.setAttribute("email", email)
        session.setAttribute("isPresentUser", "true")
        session.setAttribute("user",findUser)
        throw RedirectException(alertDTO("존재하는 회원 입니다.","/admin/publishCouponPage"))
    }

    private fun notFoundUser(response: HttpServletResponse, session: HttpSession) {
        session.removeAttribute("isPresentUser")
        session.removeAttribute("email")
        throw RedirectException(alertDTO("존재하지 않는 회원 입니다. 다시 입력해주세요.","/admin/publishCouponPage"))
    }

    fun getCouponList(requestDTO : PageRequestDTO) : PageResultDTO<CouponListDTO,Coupon>{
        val pageable = requestDTO.getPageable(Sort.by("couponId").descending())
        val booleanBuilder = getSearch(requestDTO)
        val result = couponRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Coupon, CouponListDTO> =
            Function<Coupon, CouponListDTO> { entity: Coupon? -> entity!!.entityToDto(entity) }

        return PageResultDTO<CouponListDTO,Coupon>(result,fn)
    }

    fun removeCoupon(couponId : Long){
        couponRepository.deleteById(couponId)
    }

    fun getCoupon(couponId: Long): Optional<Coupon> {
        return couponRepository.findById(couponId)
    }


    fun getCouponViewList(userId: Long):List<CouponListViewDTO> {
        val user = userRepository.findById(userId).get()
        val couponList = couponRepository.findByUser(user)
        return couponList.map {
            CouponListViewDTO(it.couponId, it.couponName, it.expired, it.status)
        }
    }

    fun updateCoupon(couponDTO : CouponDTO ,expired: LocalDate){
        val coupon = couponRepository.findById(couponDTO.couponId!!).get()
        coupon.updateCoupon(couponDTO,expired)
        couponRepository.save(coupon)
    }


    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

        val type = pageRequestDTO.type

        val booleanBuilder = BooleanBuilder()

        val qCoupon = QCoupon.coupon

        val keyword = pageRequestDTO.keyword

        val expression = qCoupon.couponId.gt(0L)

        booleanBuilder.and(expression)

        if(type.trim().isEmpty()){
            return booleanBuilder
        }

        val conditionBuilder = BooleanBuilder()

        if(type.contains("couponName")){
            conditionBuilder.or(qCoupon.couponName.contains(keyword))
        }
        if(type.contains("discountRate")){
            conditionBuilder.or(qCoupon.discountRate.eq(keyword.toInt()))
        }
        if(type.contains("email")){
            conditionBuilder.or(qCoupon.user.email.contains(keyword))
        }
        if(type == "status" && keyword == Status.ACTIVE.toString()){
            conditionBuilder.or(qCoupon.status.eq(Status.ACTIVE))
        }
        if(type == "status" && keyword == Status.IN_ACTIVE.toString()){
            conditionBuilder.or(qCoupon.status.eq(Status.IN_ACTIVE))
        }

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

    fun getMyCouponList(requestDTO: PageRequestDTO) : PageResultDTO<CouponListDTO,Coupon>{
        val pageable = requestDTO.getPageable(Sort.by("discountRate").descending())
        val booleanBuilder = getMyCouponListSearch(requestDTO)
        val result = couponRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Coupon, CouponListDTO> =
            Function<Coupon, CouponListDTO> { entity: Coupon? -> entity!!.entityToDto(entity) }

        return PageResultDTO<CouponListDTO,Coupon>(result,fn)
    }

    fun getMyCouponListSearch(pageRequestDTO: PageRequestDTO) : BooleanBuilder{
        val loginUserId = loginInfoService.getUserIdFromSession().userId
        val user = userRepository.findById(loginUserId).get() ?: null

        val booleanBuilder = BooleanBuilder()

        val qCoupon = QCoupon.coupon

        val expression = qCoupon.user.eq(user)
        booleanBuilder.and(expression)

        return booleanBuilder
    }

    fun updateCouponStatus(){
        val loginUserId = loginInfoService.getUserIdFromSession().userId
        val user = userRepository.findById(loginUserId).get() ?: null
        val findCouponsByUser = couponRepository.findCouponsByUser(user!!,LocalDate.now())
        for (coupon in findCouponsByUser) {
            coupon.status=Status.IN_ACTIVE
            couponRepository.save(coupon)
        }
    }



}