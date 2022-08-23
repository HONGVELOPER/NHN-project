package nhncommerce.project.order

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.ROLE
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponRepository
import nhncommerce.project.deliver.DeliverRepository
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.option.OptionDetailRepository
import nhncommerce.project.order.domain.*
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.review.ReviewRepository
import nhncommerce.project.user.UserRepository
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function

@Service
class OrderService(
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val optionDetailRepository: OptionDetailRepository,
    val deliverRepository: DeliverRepository,
    val reviewRepository: ReviewRepository,
    val loginInfoService: LoginInfoService
) {

    @Transactional
    fun createOrder(orderRequestDTO: OrderRequestDTO, userId: Long) {
        val optionDetail = optionDetailRepository.findById(orderRequestDTO.optionDetailId).get()


        if (optionDetail.stock -  orderRequestDTO.count < 0) {
            throw AlertException(ErrorMessage.OUT_OF_STOCK)
        }

        val productArray = IntArray(orderRequestDTO.count){0}
        for ( i in productArray.indices){
            optionDetail.extraCharge.let {
                var productPrice = optionDetail.product.price
                productPrice += it
                productArray[i] = productPrice
            }
        }
        var totalPrice = productArray.sum()
        val user = userRepository.findById(userId).get()
        val deliver = orderRequestDTO.deliverId?.let { deliverRepository.findById(it).get() }
        val coupon = orderRequestDTO.couponId?.takeIf { orderRequestDTO.couponId != 0L }?.let {
            val coupon = couponRepository.findById(it).get()
            val discountedPrice: Int = (productArray[0] * (coupon.discountRate * 0.01)).toInt()
            totalPrice -= discountedPrice
            coupon.status = Status.IN_ACTIVE
            couponRepository.save(coupon)
        }

        val order = Order(
            orderId = 0L,
            status = Status.ACTIVE,
            price = totalPrice,
            phone = orderRequestDTO.phone,
            user = user,
            coupon = coupon,
            optionDetail = optionDetail,
            count = orderRequestDTO.count,
            deliver = deliver!!,
            reviewStatus = false
        )
        optionDetail.stock = optionDetail.stock.minus(orderRequestDTO.count)
        orderRepository.save(order)
    }


    fun getUserOrderList(myOrderDTO: PageRequestDTO, userId: Long): PageResultDTO<OrderListDTO, Order> {
        val pageable = myOrderDTO.getPageable(Sort.by("updatedAt").descending())
        val booleanBuilder = userOrderListBuilder(userId, myOrderDTO)
        val resultUserOrderList = orderRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> entity!!.entityToDTO() }
        return PageResultDTO<OrderListDTO, Order>(resultUserOrderList, fn)

    }

    fun getAdminOrderList(myOrderDTO: PageRequestDTO): PageResultDTO<OrderListDTO, Order> {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val user = userRepository.findById(loginInfo.userId).get()
        if (user.role != ROLE.ROLE_ADMIN) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }

        val pageable = myOrderDTO.getPageable(Sort.by("updatedAt").descending())
        val booleanBuilder = adminOrderListBuilder(myOrderDTO)
        val resultAdminOrderList = orderRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> { entity: Order? -> entity!!.entityToDTO() }
        return PageResultDTO<OrderListDTO, Order>(resultAdminOrderList, fn)

    }


    fun getUserOrder(orderId: Long, userId: Long): Order {
        val order = orderRepository.findById(orderId).get()
        val user = userRepository.findById(userId).get()
        if (user.role == ROLE.ROLE_ADMIN && order.user.userId != userId) {
            throw AlertException(ErrorMessage.MOVE_PATH)
        }
        if (userId != order.user.userId) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        return orderRepository.findByUserAndOrderId(user, orderId)
    }


    fun getOrder(orderId: Long): Order {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val user = userRepository.findById(loginInfo.userId).get()
        if (user.role != ROLE.ROLE_ADMIN) {
            throw AlertException(ErrorMessage.WRONG_ACCESS)
        }
        return orderRepository.findById(orderId).get()
    }


    @Transactional
    fun cancelOrder(orderId: Long, userId: Long) {
        val user = userRepository.findById(userId).get()
        val order = orderRepository.findById(orderId).get()
        if(order.reviewStatus== true){
            val review = reviewRepository.findByOrder(order)
            review.status = Status.IN_ACTIVE
        }
        val optionDetailId = order.optionDetail.optionDetailId
        val optionDetail = optionDetailRepository.findById(optionDetailId).get()

        if (user.role == ROLE.ROLE_ADMIN) {
            if (order.coupon?.couponId != null) {
                val coupon = couponRepository.findById(order.coupon.couponId).get()
                coupon.status = Status.ACTIVE
            }
        } else {
            if (order.user.userId != userId) {
                throw AlertException(ErrorMessage.WRONG_ACCESS)
            }
            if (order.coupon?.couponId != null) {
                val coupon = couponRepository.findById(order.coupon.couponId).get()
                coupon.status = Status.ACTIVE
            }
        }
        optionDetail.stock += order.count
        order.status = Status.IN_ACTIVE

    }

    fun userOrderListBuilder(userId: Long, pageRequestDTO: PageRequestDTO): BooleanBuilder {
        val type = pageRequestDTO.type
        val booleanBuilder = BooleanBuilder()
        val qOrder = QOrder.order
        val keyword = pageRequestDTO.keyword
        val expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)
        val conditionBuilder = BooleanBuilder()

        if (type.contains("productName")) {
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword))
                .and(qOrder.user.userId.eq(userId))
        }
        if (type.contains("price")) {
            if(keyword.isNotBlank()){
                try{
                    conditionBuilder.or(qOrder.price.eq(keyword.toInt())).and(qOrder.user.userId.eq(userId))
                }catch (e : Exception){
                    throw AlertException(ErrorMessage.STRING_TO_INT_CONVERSION_ERROR)
                }
            }
        }
        if (type == "status" && keyword == "주문 완료") {
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE)).and(qOrder.user.userId.eq(userId))
        }
        if (type == "status" && keyword == "주문 취소") {
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE)).and(qOrder.user.userId.eq(userId))
        }
        if (type.contains("address")) {
            conditionBuilder.or(qOrder.deliver.address.contains(keyword)).and(qOrder.user.userId.eq(userId))
        }

        conditionBuilder.and(qOrder.user.userId.eq(userId))
        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

    fun adminOrderListBuilder(pageRequestDTO: PageRequestDTO): BooleanBuilder {
        val type = pageRequestDTO.type
        val booleanBuilder = BooleanBuilder()
        val qOrder = QOrder.order
        val keyword = pageRequestDTO.keyword
        val expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)

        if (type == null || type.trim().isEmpty()) {
            return booleanBuilder
        }

        var conditionBuilder = BooleanBuilder()

        if (type.contains("productName")) {
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword))
        }
        if (type.contains("price")) {
            if(keyword.isNotBlank()){
                try{
                    conditionBuilder.or(qOrder.price.eq(keyword.toInt()))
                }catch (e : Exception){
                    throw AlertException(ErrorMessage.STRING_TO_INT_CONVERSION_ERROR)
                }
            }
        }
        if (type == "status" && keyword == "주문 완료") {
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE))
        }
        if (type == "status" && keyword == "주문 취소") {
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE))
        }
        if (type.contains("address")) {
            conditionBuilder.or(qOrder.deliver.address.contains(keyword))
        }
        if (type.contains("user")) {
            conditionBuilder.or(qOrder.user.name.contains(keyword))
        }

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }
}

