package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.user.UserRepository
import nhncommerce.project.user.domain.User
import nhncommerce.project.util.alert.AlertService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PutMapping
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletResponse

@Service
class OrderService (
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val productRepository: ProductRepository,
    val alertService: AlertService

){
    fun createOrderDTO(orderRequestDTO: OrderRequestDTO): OrderDTO {
        val user = orderRequestDTO.userId?.let { userRepository.findByUserId(it) }
        val coupon = orderRequestDTO.couponId?.let { couponRepository.findByCouponId(it) }
        val product = orderRequestDTO.productId?.let { productRepository.findByProductId(it) }
        var productPrice = orderRequestDTO.price
        val totalProductPrice: Int = (productPrice!! -  (productPrice!! * (coupon!!.discountRate * 0.01))).toInt()
        val orderDTO = OrderDTO(orderId = null, status = Status.ACTIVE,totalProductPrice,
            orderRequestDTO.phone,user,coupon,product)

        return orderDTO
    }

    fun createOrder(orderDTO: OrderDTO): Order {
        val order = orderDTO.toEntity()
        return orderRepository.save(order)
    }

    fun toOrderListDTO(order: Order): OrderListDTO{
        return OrderListDTO(order.orderId, order.status!!,order.price,order.phone,order.userId,order.couponId
        ,order.productId,order.createdAt)
    }

    /**
     * user 조회
     * */
    fun getMyOrderList(myOrderDTO: PageRequestDTO,userId: Long) : PageResultDTO<OrderListDTO,Order>{ val Id: Long = 1L //test value
        println("두번째")
        val pageable = myOrderDTO.getPageable(Sort.by("createdAt").ascending())
//        var booleanBuilder = getSearch(myOrderDTO)
        println("세번째")

        var booleanBuilder2=getUserSearch(userId,myOrderDTO)
        val result2 = orderRepository.findAll(booleanBuilder2,pageable)
        println("네번째")
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> toOrderListDTO(entity!!) }
        println("다섯번째")
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
        return orderRepository.findByuserIdAndOrderId(user,orderId)
    }
    fun cancelMyOrder(orderDTO: OrderDTO,orderId: Long, userId: Long,response: HttpServletResponse){
        val user = userRepository.findByUserId(userId)
        var order = orderRepository.findByuserIdAndOrderId(user,orderId)
        order.get().status = Status.IN_ACTIVE
        orderRepository.save(order.get())

        //todo 1. 주문 취소시 제한 사항을 생각해보자 일단 임시로 무지성 삭제
        //todo 2. 주문 취소시 쿠폰은 돌려줘야하는지? 생각해보자
        alertService.alertMessage("주문이 취소되었습니다.","/orders",response)
    }


    fun getUserSearch(userId : Long,pageRequestDTO: PageRequestDTO):BooleanBuilder{
        var booleanBuilder = BooleanBuilder()
        var qOrder = QOrder.order
        var expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)
        var conditionBuilder = BooleanBuilder()
        val user = userRepository.findByUserId(userId)
        conditionBuilder.and(qOrder.userId.eq(user))
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
            conditionBuilder.or(qOrder.productId.productName.contains(keyword))
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

