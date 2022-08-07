package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.coupon.domain.CouponDTO
import nhncommerce.project.coupon.domain.CouponRequestDTO
import nhncommerce.project.deliver.DeliverRepository
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.option.OptionService
import nhncommerce.project.option.domain.OptionDetailDTO
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.util.alert.AlertService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
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
    val optionService: OptionService

){
    fun createOrder(orderRequestDTO: OrderRequestDTO,response:HttpServletResponse) {
        val user = orderRequestDTO.userId?.let { userRepository.findByUserId(it) }
        val coupon = orderRequestDTO.couponId?.let { couponRepository.findByCouponId(it) }
        val optiondetail = orderRequestDTO.optionDetailId?.let { optionDetailRepository.findByOptionDetailId(it) }
        val deliver = orderRequestDTO.deliverId?.let { deliverRepository.findById(it).get() }
        var productPrice = orderRequestDTO.price!! + optiondetail!!.extraCharge!!
        println("추가금액 합친 총 금액 : " + productPrice )
        val totalProductPrice: Int = (productPrice!! -  (productPrice!! * (coupon!!.discountRate * 0.01))).toInt()

        println("계산할 금액 : " + totalProductPrice)
        val orderDTO = OrderDTO(
            orderId = null, status = Status.ACTIVE,totalProductPrice,
            orderRequestDTO.phone,user,coupon,optiondetail,deliver)

        val couponDTO = CouponRequestDTO(
                couponId = coupon.couponId,
                user = user,
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
        alertService.alertMessage("주문이 완료되었습니다.","/products",response) // userProductPage로 바꿔야힘.

    }


    fun toOrderListDTO(order: Order): OrderListDTO{
        //todo 출력되어야 할것들.
        //todo 상품이름, 상품옵션, 옵션값, 가격, 쿠폰, 배송지, 주문 시간
        return OrderListDTO(order.orderId, order.status!!,order.price,order.phone,order.user,order.coupon
        ,order.optionDetail,order.deliver,order.createdAt)
    }

    /**
     * user 조회
     * */
    fun getMyOrderList(myOrderDTO: PageRequestDTO,userId: Long) : PageResultDTO<OrderListDTO,Order>{ val Id: Long = 1L //test value
        val pageable = myOrderDTO.getPageable(Sort.by("createdAt").ascending())
        var booleanBuilder2=getUserSearch(userId,myOrderDTO)
        val result2 = orderRepository.findAll(booleanBuilder2,pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> toOrderListDTO(entity!!) }
        return PageResultDTO<OrderListDTO,Order>(result2,fn)

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
    fun cancelMyOrder(orderDTO: OrderDTO,orderId: Long, userId: Long,response: HttpServletResponse){
        val user = userRepository.findByUserId(userId)
        var order = orderRepository.findByUserAndOrderId(user,orderId)
        order.get().status = Status.IN_ACTIVE
        orderRepository.save(order.get())
        //todo 쿠폰 상태 ACTIVE로 변경하는 서비스 삽입

        alertService.alertMessage("주문이 취소되었습니다.","/orders",response)
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

