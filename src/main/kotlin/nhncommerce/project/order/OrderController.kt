package nhncommerce.project.order

import nhncommerce.project.coupon.CouponService
import nhncommerce.project.deliver.DeliverService
import nhncommerce.project.order.domain.OrderDTO
import nhncommerce.project.order.domain.OrderRequestDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletResponse

@Controller
class OrderController (
    val orderService: OrderService,
    val couponService: CouponService,
    val deliverService: DeliverService)

{
    /**
     * 상품 주문 페이지
     */
    @GetMapping("/orderProductPage")
    fun orderProductPage(model: Model): String {
        val orderRequestDTO = OrderRequestDTO()
        //userId 테스트 값 나중에 프론트에서 받아와야함. 쿠키든 세션이든
        var num = 1
        val couponListViewDTO = couponService.getCouponViewList(num.toLong())
        val deliverListviewDTO = deliverService.getDeliverViewList(num.toLong())

        model.addAttribute("deliverListViewDTO", deliverListviewDTO)
        model.addAttribute("couponListViewDTO", couponListViewDTO)
        model.addAttribute("orderRequestDTO", orderRequestDTO)
        return "order/orderProduct"
    }

    /**
     * 상품 주문
     */
    @PostMapping("/orders")
    fun orderProduct(orderRequestDTO: OrderRequestDTO, response: HttpServletResponse){
        println(orderRequestDTO.couponId)
        println(orderRequestDTO.deliverId)
        val order = orderService.createOrder(orderRequestDTO,response)
//        orderService.createOrder(order)
//        return "order/success"
    }

    /**
     * 나의 주문내역 조회 페이지
     */
    @GetMapping("/myOrderListPage")
    fun getMyOrderListPage(): String {
        return "order/myOrderList"
    }

    /**
     * 나의 주문내역 전체 조회
     */
    @GetMapping("/orders/{userId}")
    fun orderList(@PathVariable("userId")userId: Long,pageRequestDTO: PageRequestDTO, model: Model):String{
        model.addAttribute("orders",orderService.getMyOrderList(pageRequestDTO,userId))
        return "order/myOrderList"
    }

    /**
     * 모든 사용자 주문내역 전체 조회
     */
    @GetMapping("/orders")
    fun orderListTest(pageRequestDTO: PageRequestDTO, model: Model): String {
        model.addAttribute("orders", orderService.getOrderList(pageRequestDTO))
        return "order/myOrderList"
    }

    /**
     * 사용자 주문내역 단건조회
     */
    @GetMapping("/orders/{orderId}/users/{userId}")
    fun getMyOrder(@PathVariable("orderId")orderId: Long,@PathVariable("userId")userId: Long,
    model: Model):String{
        model.addAttribute("orderInfo",orderService.getOrder(orderId,userId))
        return "order/MyOrder"
    }

    /**
     * 나의 주문 취소
     */
    @PutMapping("/orders/{orderId}/users/{userId}")
    fun cancelMyOrder(@PathVariable("orderId")orderId:Long,@PathVariable("userId")userId: Long,
    orderDTO:OrderDTO, response: HttpServletResponse
    ): String {
        orderService.cancelMyOrder(orderDTO,orderId,userId,response)
        return "order/myOrderList"
    }
}