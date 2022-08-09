package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.coupon.domain.Coupon
import nhncommerce.project.deliver.DeliverRepository
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.UserRepository
import nhncommerce.project.util.alert.AlertService
import nhncommerce.project.util.alert.alertDTO
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
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
    val alertService: AlertService
){



    fun createOrder(orderRequestDTO: OrderRequestDTO, userId: Long,response:HttpServletResponse) {
        val user = userRepository.findByUserId(userId)
        val optionDetail = orderRequestDTO.optionDetailId.let {
            optionDetailRepository.findByOptionDetailId(it)
        }

        if (optionDetail.stock!! < 1){
            throw RedirectException(alertDTO("주문하신 제품의 재고가 소진되었습니다. 죄송합니다.", "/products"))
        }
        optionDetail.stock = optionDetail.stock?.minus(1)
        var product = optionDetailRepository.findByOptionDetailId(orderRequestDTO.optionDetailId)
        var productPrice = product.product!!.price  //DB에 저장된 해당되는 옵션을 가진 상품의 가격을 불러온다.

        optionDetail.extraCharge?.let{
            productPrice += optionDetail.extraCharge!!
        }
        if (orderRequestDTO.deliverId == null){
            throw RedirectException(alertDTO("배송지가 없습니다. 배송지를 등록해주세요.", "/api/delivers/createForm"))
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
        val pageable = myOrderDTO.getPageable(Sort.by("createdAt").descending())
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
     * 주문 내역 단건 조회 - user
     * */
    fun getUserOrder(orderId:Long,userId: Long) : Optional<Order> {
        val user = userRepository.findByUserId(userId)

        val order = orderRepository.findByUserAndOrderId(user,orderId)

        return order


    }

    /**
     * 주문 내역 단건 조회 - admin
     * */
    fun getOrder(orderId: Long):  Optional<Order>{
        val order = orderRepository.findByOrderId(orderId)
        return order
    }


    /**
     * 주문 취소 - 사용자
     * */
    fun cancelMyOrder(orderDTO: OrderDTO,orderId: Long, userId: Long,response: HttpServletResponse){
        val user = userRepository.findByUserId(userId)
        var order = orderRepository.findByUserAndOrderId(user,orderId)
        if(order.isPresent()) {

            if (order.get().user!!.userId != userId) {
                throw RedirectException(alertDTO("잘못된 접근입니다.", "/orders"))
            }
            if (orderDTO.coupon?.couponId != null) {
                var coupon = couponRepository.findByCouponId(orderDTO.coupon!!.couponId!!)
                coupon.status = Status.ACTIVE
                couponRepository.save(coupon)
            }
            order.get().status = Status.IN_ACTIVE
            order.get().updatedAt = LocalDateTime.now()
            orderRepository.save(order.get())
        }
        alertService.alertMessage("주문이 취소되었습니다.","/orders",response)
    }
    /**
     * 주문 취소 - 관리자
     * */
    fun cancelOrder(orderDTO:OrderDTO, orderId: Long, response: HttpServletResponse){
        val order =orderRepository.findByOrderId(orderId)
        if(order.isPresent){

            if (orderDTO.coupon?.couponId != null) {
                var coupon = couponRepository.findByCouponId(orderDTO.coupon!!.couponId!!)
                coupon.status = Status.ACTIVE
                couponRepository.save(coupon)
            }

            order.get().status = Status.IN_ACTIVE
            order.get().updatedAt = LocalDateTime.now()
            orderRepository.save(order.get())
        }
        alertService.alertMessage("주문이 취소되었습니다.","/admin/orders",response)

    }


    fun getUserSearch(userId : Long,pageRequestDTO: PageRequestDTO):BooleanBuilder{
        var type = pageRequestDTO.type
        var booleanBuilder = BooleanBuilder()
        var qOrder = QOrder.order
        var keyword = pageRequestDTO.keyword
        var expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)
        var conditionBuilder = BooleanBuilder()
        val user = userRepository.findByUserId(userId)

        if(type.contains("productName")){
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword)).and(qOrder.user.eq(user))
        }
        if(type.contains("price")){
            conditionBuilder.or(qOrder.price.eq(keyword.toInt())).and(qOrder.user.eq(user))
        }
        if(type == "status" && keyword == "주문 완료"){
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE)).and(qOrder.user.eq(user))
        }
        if(type == "status" && keyword == "주문 취소"){
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE)).and(qOrder.user.eq(user))
        }
        if(type.contains("address")){
            conditionBuilder.or(qOrder.deliver.address.contains(keyword)).and(qOrder.user.eq(user))
        }

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
        if(type == "status" && keyword == "주문 완료"){
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE))
        }
        if(type == "status" && keyword == "주문 취소"){
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE))
        }
        if(type.contains("address")){
            conditionBuilder.or(qOrder.deliver.address.contains(keyword))
        }

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }
}

