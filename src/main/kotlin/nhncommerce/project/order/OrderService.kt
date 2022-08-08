package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.coupon.domain.CouponRequestDTO
import nhncommerce.project.deliver.DeliverRepository
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.option.domain.OptionDetailDTO
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.user.UserRepository
import nhncommerce.project.util.alert.AlertService
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
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
    val loginInfoService: LoginInfoService,
    val productRepository: ProductRepository

){



    fun createOrder(orderRequestDTO: OrderRequestDTO, response:HttpServletResponse) {
        println("!~~~~~~~~~~~!")
        println(orderRequestDTO.userId)
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val user = userRepository.findByUserId(loginInfo.userId)
        val optionDetail = orderRequestDTO.optionDetailId.let {
            optionDetailRepository.findByOptionDetailId(it)
        }

        optionDetail.stock = optionDetail.stock?.minus(1)
//        var productPrice = orderRequestDTO.price
        var product = optionDetailRepository.findByOptionDetailId(orderRequestDTO.optionDetailId)
        var productPrice = product.product!!.price  //DB에 저장된 해당되는 옵션을 가진 상품의 가격을 불러온다.
        if (optionDetail.extraCharge != null) {
            productPrice += optionDetail.extraCharge!!
        }
        val deliver = orderRequestDTO.deliverId?.let { deliverRepository.findById(it).get() }
        var coupon: Coupon? = null
        if (orderRequestDTO.couponId != 0.toLong()){
            coupon = orderRequestDTO.couponId.let { couponRepository.findByCouponId(it!!) }
            val saledPrice: Int = (productPrice * (coupon.discountRate * 0.01)).toInt()
            productPrice -= saledPrice
            coupon.status = Status.IN_ACTIVE
            couponRepository.save(coupon)
        }

        val orderDTO = OrderDTO(
            status = Status.ACTIVE, productPrice,
            orderRequestDTO.phone, user, coupon, optionDetail,deliver
        )

        val order = orderDTO.toEntity()
        orderRepository.save(order)
        optionDetailRepository.save(optionDetail)
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

        if (orderDTO.coupon?.couponId != null){
            var coupon = couponRepository.findByCouponId(orderDTO.coupon!!.couponId!!)
            coupon.status = Status.IN_ACTIVE
            couponRepository.save(coupon)
        }

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

