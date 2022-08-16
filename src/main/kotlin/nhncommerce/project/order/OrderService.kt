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
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletResponse

@Service
class OrderService(
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val couponRepository: CouponRepository,
    val optionDetailRepository: OptionDetailRepository,
    val deliverRepository: DeliverRepository
) {

    fun createOrder(orderRequestDTO: OrderRequestDTO, userId: Long) {

        val user = userRepository.findById(userId).get()
        val optionDetail = optionDetailRepository.findById(orderRequestDTO.optionDetailId).get() //todo : get()을 왜쓰나 따로 정의하면 get 안써도된다.
        //todo : otional을 좀 자바스러운 스타일 , 코틀린 스럽게는 엔티티로 바로 반환 findByIdOrNull 이런식으로 하는게 컨트롤하기가 좋다.
        if (optionDetail.stock!! < 1) {
            throw RedirectException(alertDTO("주문하신 제품의 재고가 소진되었습니다. 죄송합니다.", "/user"))
        }

        optionDetail.stock = optionDetail.stock?.minus(1)
        var productPrice = optionDetail.product!!.price + optionDetail.extraCharge.takeIf { it != null }
        //todo : 할당할때 같이 하자 , takeif를 사용하면 된다. => 한줄로 줄일수있다.

        optionDetail.extraCharge?.let {
            productPrice += it
            // 애초에 가독성이 안좋다. let이 안어울림
            // if null 체크는 코틀린에서 권장하지않음 let에서 하자
            //todo : 그냥 it만 쓰면 된다.
        }

        //todo : controller에서 있어야할 예외 처리이다. , 예외처리는 한쪽에 몰아둔다.
        if (orderRequestDTO.deliverId == null) {
            throw RedirectException(alertDTO("배송지가 없습니다. 배송지를 등록해주세요.", "/api/delivers/createForm"))
        }

        val deliver = orderRequestDTO.deliverId?.let { deliverRepository.findById(it).get() }

        var coupon: Coupon? = null // todo : 쿠폰을 안썼을 경우 null 로 초기화 해주었다. 더좋은 방법이 있어보임
        //todo : 코틀린에서 if null 은 잘 안해
        //todo : toLong이 아닌 L 로 받는다.
        if (orderRequestDTO.couponId != 0.toLong()) {
            coupon = orderRequestDTO.couponId?.let { couponRepository.findById(it).get() }
            val saledPrice: Int = (productPrice * (coupon!!.discountRate * 0.01)).toInt()
            productPrice -= saledPrice
            coupon.status = Status.IN_ACTIVE
            couponRepository.save(coupon)
        }

        val orderDTO = OrderDTO(
            status = Status.ACTIVE, productPrice,
            orderRequestDTO.phone!!, user, coupon, optionDetail, deliver!!,reviewStatus = false
        )

        //todo : 변수선언이 너무 많다. 정리 필요
        val order = orderDTO.dtoToEntity()
        orderRepository.save(order)
        optionDetailRepository.save(optionDetail)
        //todo : 변경감지가 있기때문에 save 안해도된다.

    }


    /**
     * user 조회
     * */
    fun getMyOrderList(myOrderDTO: PageRequestDTO, userId: Long): PageResultDTO<OrderListDTO, Order> {
        val pageable = myOrderDTO.getPageable(Sort.by("updatedAt").descending())
        var booleanBuilder = getUserSearch(userId, myOrderDTO)
        val result = orderRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> {entity: Order? -> entity!!.entityToDTO() }
        return PageResultDTO<OrderListDTO, Order>(result, fn)

    }

    /**
     * admin 조회
     * */
    //todo :노란줄 다 바꾸기
    fun getOrderList(myOrderDTO: PageRequestDTO): PageResultDTO<OrderListDTO, Order> {
        val pageable = myOrderDTO.getPageable(Sort.by("updatedAt").descending())
        var booleanBuilder = getSearch(myOrderDTO)
        val result = orderRepository.findAll(booleanBuilder, pageable)
        val fn: Function<Order, OrderListDTO> =
            Function<Order, OrderListDTO> {entity: Order? -> entity!!.entityToDTO() }
        return PageResultDTO<OrderListDTO, Order>(result, fn)
    }

    /**
     * 주문 내역 단건 조회 - user
     * */
    fun getUserOrder(orderId: Long, userId: Long): Order {
        val user = userRepository.findById(userId).get()
        val order = orderRepository.findByUserAndOrderId(user, orderId)
        return order
    }

    /**
     * 주문 내역 단건 조회 - admin
     * */
    fun getOrder(orderId: Long): Order {
        val order = orderRepository.findById(orderId).get()
        return order
    }


    /**
     * 주문 취소 - 사용자
     * */
    //todo : 중복코드 하나로 합치고 역할을 검사하자
    fun cancelMyOrder(orderId: Long, userId: Long) {
        val user = userRepository.findById(userId).get()
        val order = orderRepository.findByUserAndOrderId(user, orderId)

            if (order.user!!.userId != userId) {
                throw RedirectException(alertDTO("잘못된 접근입니다.", "/api/orders"))
            }
            if (order.coupon?.couponId != null) {
                val coupon = couponRepository.findById(order.coupon!!.couponId!!).get()
                coupon.status = Status.ACTIVE
                couponRepository.save(coupon)
            }

            order.status = Status.IN_ACTIVE
            orderRepository.save(order)
    }

    /**
     * 주문 취소 - 관리자
     * */
    fun cancelOrder(orderId: Long) {
        val order = orderRepository.findById(orderId).get()

            if (order.coupon?.couponId != null) {

                //todo : 위에서 널체크 했기때문에 !! 안해도 된다.
                var coupon = couponRepository.findById(order.coupon!!.couponId!!).get()
                coupon.status = Status.ACTIVE
                couponRepository.save(coupon)
            }

            order.status = Status.IN_ACTIVE
            orderRepository.save(order)

    }


    //todo : 함수명이 이상, 디비로 조회해서
    fun getUserSearch(userId: Long, pageRequestDTO: PageRequestDTO): BooleanBuilder {
        var type = pageRequestDTO.type
        var booleanBuilder = BooleanBuilder()
        var qOrder = QOrder.order
        var keyword = pageRequestDTO.keyword
        var expression = qOrder.orderId.gt(0L)
        booleanBuilder.and(expression)
        var conditionBuilder = BooleanBuilder()
        val user = userRepository.findById(userId).get() //todo : 이거 필요 없다. , 그냥 userId로 조회하면 된다.

        if (type.contains("productName")) {
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword)).and(qOrder.user.eq(user))
        }
        if (type.contains("price")) {
            conditionBuilder.or(qOrder.price.eq(keyword.toInt())).and(qOrder.user.eq(user))
        }
        if (type == "status" && keyword == "주문 완료") {
            conditionBuilder.or(qOrder.status.eq(Status.ACTIVE)).and(qOrder.user.eq(user))
        }
        if (type == "status" && keyword == "주문 취소") {
            conditionBuilder.or(qOrder.status.eq(Status.IN_ACTIVE)).and(qOrder.user.eq(user))
        }
        if (type.contains("address")) {
            conditionBuilder.or(qOrder.deliver.address.contains(keyword)).and(qOrder.user.eq(user))
        }

        conditionBuilder.and(qOrder.user.eq(user))
        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {
        var type = pageRequestDTO.type

        var booleanBuilder = BooleanBuilder()

        var qOrder = QOrder.order

        var keyword = pageRequestDTO.keyword

        var expression = qOrder.orderId.gt(0L)

        booleanBuilder.and(expression)

        if (type == null || type.trim().isEmpty()) {
            return booleanBuilder
        }

        var conditionBuilder = BooleanBuilder()

        if (type.contains("productName")) {
            conditionBuilder.or(qOrder.optionDetail.product.productName.contains(keyword))
        }
        if (type.contains("price")) {
            conditionBuilder.or(qOrder.price.eq(keyword.toInt()))
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

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }
}

