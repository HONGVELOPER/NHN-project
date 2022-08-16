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
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*
import java.util.function.Function

@Service
class CouponService(
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val loginInfoService: LoginInfoService
) {

    @Transactional
    fun createCoupon(couponDTO: CouponDTO, expired: LocalDate, email: String) {
        val findUser = userRepository.findByEmail(email) ?: notFoundUser()
        val coupon = couponDTO.dtoToEntity(findUser as User,expired)
        couponRepository.save(coupon)
    }

    @Transactional
    fun createEventCoupon(userId : Long, discountRate : Int,  expired: LocalDate, couponName : String){
        val user = userRepository.findById(userId).get()
        val coupon = Coupon(0L,user, Status.ACTIVE, couponName, discountRate, expired)
        couponRepository.save(coupon)
    }

    private fun notFoundUser() {
        throw RedirectException(alertDTO("존재하지 않는 회원 입니다. 다시 입력해주세요.","/admin/publishCouponPage"))
    }

    fun getCouponList(requestDTO : PageRequestDTO) : PageResultDTO<CouponListDTO,Coupon>{
        val pageable = requestDTO.getPageable(Sort.by("couponId").descending())
        val booleanBuilder = getCouponSearch(requestDTO)
        val result = couponRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Coupon, CouponListDTO> =
            Function<Coupon, CouponListDTO> { entity: Coupon? -> entity?.entityToDto(entity) }

        return PageResultDTO<CouponListDTO,Coupon>(result,fn)
    }

    fun removeCoupon(couponId : Long){
        couponRepository.deleteById(couponId)
    }

    fun getCoupon(couponId: Long): Optional<Coupon> {
        return couponRepository.findById(couponId)
    }
    
    fun getCouponViewList(userId: Long):List<CouponListViewDTO> {
        val list = mutableListOf<CouponListViewDTO>()
        val user = userRepository.findById(userId).get()
        val couponList = couponRepository.findByUser(user)
        couponList.map {
            val CouponListDTO = CouponListViewDTO(it.couponId, it.couponName, it.expired, it.status)
            list.add(CouponListDTO)
        }
        return list.toList()
    }

    @Transactional
    fun updateCoupon(couponDTO : CouponDTO ,expired: LocalDate){
        val coupon = couponRepository.findById(couponDTO.couponId).get()
        coupon.updateCoupon(couponDTO,expired)
    }


    fun getCouponSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

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
            Function<Coupon, CouponListDTO> { entity: Coupon? -> entity?.entityToDto(entity) }

        return PageResultDTO<CouponListDTO,Coupon>(result,fn)
    }

    fun getMyCouponListSearch(pageRequestDTO: PageRequestDTO) : BooleanBuilder{
        val loginUserId = loginInfoService.getUserIdFromSession().userId

        val booleanBuilder = BooleanBuilder()

        val qCoupon = QCoupon.coupon

        val expression = qCoupon.user.userId.eq(loginUserId)
        booleanBuilder.and(expression)

        return booleanBuilder
    }

    @Transactional
    fun updateCouponStatus(){
        val loginUserId = loginInfoService.getUserIdFromSession().userId
        val user = userRepository.findById(loginUserId).get()
        val findCouponsByUser = couponRepository.findCouponsByUser(user,LocalDate.now())
        for (coupon in findCouponsByUser) {
            coupon.status=Status.IN_ACTIVE
        }
    }

}