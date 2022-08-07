package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.coupon.domain.CouponRequestDTO
import nhncommerce.project.deliver.DeliverRepository
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.option.domain.OptionDetailDTO
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.util.alert.AlertService
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletResponse

@Service
class OrderService (
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val optionDetailRepository: OptionDetailRepository,
    val deliverRepository: DeliverRepository,
    val alertService: AlertService,
    val couponService: CouponService,
    val loginInfoService: LoginInfoService

){
    fun createOrder(orderRequestDTO: OrderRequestDTO,response:HttpServletResponse) {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val user = userRepository.findByUserId(loginInfo.userId)
        val coupon = orderRequestDTO.couponId?.let { couponRepository.findByCouponId(it) }
        val optiondetail = orderRequestDTO.optionDetailId?.let { optionDetailRepository.findByOptionDetailId(it) }
        val deliver = orderRequestDTO.deliverId?.let { deliverRepository.findById(it).get() }
        var productPrice = orderRequestDTO.price!! + optiondetail!!.extraCharge!!
        val totalProductPrice: Int = (productPrice!! -  (productPrice!! * (coupon!!.discountRate * 0.01))).toInt()

        val orderDTO = OrderDTO(
            orderId = null, status = Status.ACTIVE,totalProductPrice,
            orderRequestDTO.phone,user,coupon,optiondetail,deliver)

        val couponDTO = CouponRequestDTO(
                couponId = coupon.couponId,
                user = coupon.user,
                status = Status.IN_ACTIVE,
                couponName = coupon.couponName,
                discountRate = coupon.discountRate,
                expired = coupon.expired
        )

        val optionDetailDTO = OptionDetailDTO(
            optionDetailId = optiondetail!!.optionDetailId,
            status = optiondetail.status,
            extraCharge = optiondetail.extraCharge,
            stock = optiondetail.stock!! - 1,
            num = optiondetail.num,
            name = optiondetail.name,
            product = optiondetail.product,
            option1 = optiondetail.option1,
            option2 = optiondetail.option2,
            option3 = optiondetail.option3
        )

        val useCoupon = couponService.toEntity(couponDTO)
        couponRepository.save(useCoupon)

        val decreaseStock = optionDetailDTO.toEntity()
        optionDetailRepository.save(decreaseStock)

        val order = orderDTO.toEntity()
        orderRepository.save(order)
        alertService.alertMessage("주문이 완료되었습니다.","/products",response) // 사용자 메인 페이지로 바꿔야힘.

    }


    fun toOrderListDTO(order: Order): OrderListDTO{
        return OrderListDTO(order.orderId, order.status!!,order.price,order.phone,order.user,order.coupon
        ,order.optionDetail,order.deliver,order.createdAt, order.updatedAt)
    }

    /**
     * user 조회
     * */
    fun getMyOrderList(myOrderDTO: PageRequestDTO,userId: Long) : PageResultDTO<OrderListDTO,Order>{
        val pageable = myOrderDTO.getPageable(Sort.by("createdAt").ascending())
        var booleanBuilder=getUserSearch(userId,myOrderDTO)
        val result = orderRepository.findAll(booleanBuilder,pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> toOrderListDTO(entity!!) }
        return PageResultDTO<OrderListDTO,Order>(result,fn)

    }

    /**
     * admin 조회
     * */
    fun getOrderList(myOrderDTO: PageRequestDTO) : PageResultDTO<OrderListDTO,Order>{
        val pageable = myOrderDTO.getPageable(Sort.by("createdAt").descending())
        var booleanBuilder = getSearch(myOrderDTO)
        val result = orderRepository.findAll(booleanBuilder,pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> toOrderListDTO(entity!!) }
        return PageResultDTO<OrderListDTO,Order>(result,fn)

    }

    /**
     * 주문 내역 단건 조회
     * */
    fun getOrder(orderId:Long,userId: Long) : Optional<Order> {
        val user = userRepository.findByUserId(userId)
        return orderRepository.findByUserAndOrderId(user,orderId)
    }


    /**
     * 주문 취소
     * */
    fun cancelMyOrder(orderDTO: OrderDTO,orderId: Long, userId: Long,response: HttpServletResponse){
        val user = userRepository.findByUserId(userId)
        var order = orderRepository.findByUserAndOrderId(user,orderId)
        order.get().status = Status.IN_ACTIVE
        order.get().updatedAt = LocalDateTime.now()
        orderRepository.save(order.get())

        val couponDTO = CouponRequestDTO(
            couponId = order.get().coupon!!.couponId,
            user = order.get().coupon!!.user,
            status = Status.ACTIVE,
            couponName = order.get().coupon!!.couponName,
            discountRate = order.get().coupon!!.discountRate,
            expired = order.get().coupon!!.expired
        )
        val useCoupon = couponService.toEntity(couponDTO)
        couponRepository.save(useCoupon)
        alertService.alertMessage("주문이 취소되었습니다.","/orders/myOrderList",response)
    }



    fun getUserSearch(userId : Long,pageRequestDTO: PageRequestDTO):BooleanBuilder{
        var booleanBuilder = BooleanBuilder()
        var qOrder = QOrder.order
        var expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)
        var conditionBuilder = BooleanBuilder()
        val user = userRepository.findByUserId(userId)
        conditionBuilder.and(qOrder.user.eq(user))
        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder{
        var type = pageRequestDTO.type

        var booleanBuilder = BooleanBuilder()

        var qOrder = QOrder.order

        var keyword = pageRequestDTO.keyword

        var expression = qOrder.orderId.gt(0L)

        booleanBuilder.and(expression)

        if(type == null || type.trim().isEmpty()){
            return booleanBuilder
        }

        var conditionBuilder = BooleanBuilder()

        if(type.contains("productName")){
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword))
        }
        if(type.contains("price")){
            conditionBuilder.or(qOrder.price.eq(keyword.toInt()))
        }
        if(type == "status" && keyword == Status.ACTIVE.toString()){
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE))
        }
        if(type == "status" && keyword == Status.IN_ACTIVE.toString()){
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE))
        }


        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }
}

