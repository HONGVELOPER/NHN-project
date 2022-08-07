package nhncommerce.project.coupon


import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.CategoryListDTO
import nhncommerce.project.coupon.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.QUser.user
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.AlertService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import java.util.function.Function
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Service
class CouponService(
    val userRepository: UserRepository,
    val alertService: AlertService,
    val couponRepository: CouponRepository
) {

    fun dtoToEntity(couponDTO: CouponDTO, user : User, expired : LocalDate): Coupon{
        val coupon = Coupon(couponName = couponDTO.couponName, discountRate = couponDTO.discountRate,
            expired =expired, user = user)
        return coupon
    }

    fun entityToDto(coupon : Coupon): CouponListDTO{
        val couponListDTO = CouponListDTO(coupon.couponId,coupon.user.email, coupon.status, coupon.couponName, coupon.discountRate,
                                        coupon.expired, coupon.createdAt, coupon.updatedAt)
        return couponListDTO
    }

    /**
     * 주문 시 쿠폰 상태 비활성화 하기위한 status 수정
     * */
    fun toEntity(couponRequestDTO: CouponRequestDTO): Coupon{
        val coupon = Coupon(couponId = couponRequestDTO.couponId , user = couponRequestDTO.user!!,
            status = couponRequestDTO.status, couponName = couponRequestDTO.couponName,
            discountRate = couponRequestDTO.discountRate, expired = couponRequestDTO.expired)
        return coupon
    }

    fun createCoupon(couponDTO: CouponDTO, expired: LocalDate, session: HttpSession) {
        var user = session.getAttribute("user")
        val coupon = dtoToEntity(couponDTO, user as User,expired)
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

    fun getCouponList(requestDTO : PageRequestDTO) : PageResultDTO<CouponListDTO,Coupon>{
        val pageable = requestDTO.getPageable(Sort.by("couponId").descending())
        var booleanBuilder = getSearch(requestDTO)
        val result = couponRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Coupon, CouponListDTO> =
            Function<Coupon, CouponListDTO> { entity: Coupon? -> entityToDto(entity!!) }

        return PageResultDTO<CouponListDTO,Coupon>(result,fn)
    }

    fun removeCoupon(couponId : Long){
        couponRepository.deleteById(couponId)
    }

    fun getCoupon(couponId: Long): Optional<Coupon> {
        return couponRepository.findById(couponId)
    }

    /**
     * 주문하기Page 에서 사용자의 사용가능한 쿠폰들 가져오기
     * */
    fun getCouponViewList(userId: Long):List<CouponListViewDTO> {
        val list = mutableListOf<CouponListViewDTO>()
        val user = userRepository.findByUserId(userId)
        val couponList = couponRepository.findByUser(user)
        couponList.map {
            val CouponListDTO = CouponListViewDTO(it.couponId, it.couponName, it.status)
            list.add(CouponListDTO)
        }
        return list.toList()
    }

    fun updateCoupon(couponDTO : CouponDTO ,expired: LocalDate){
        var coupon = couponRepository.findById(couponDTO.couponId!!).get()
        coupon.updateCoupon(couponDTO,expired)
        couponRepository.save(coupon)
    }


    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

        var type = pageRequestDTO.type

        var booleanBuilder = BooleanBuilder()

        var qCoupon = QCoupon.coupon

        var keyword = pageRequestDTO.keyword

        var expression = qCoupon.couponId.gt(0L)

        booleanBuilder.and(expression)

        if(type == null || type.trim().isEmpty()){
            return booleanBuilder
        }

        var conditionBuilder = BooleanBuilder()

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

}