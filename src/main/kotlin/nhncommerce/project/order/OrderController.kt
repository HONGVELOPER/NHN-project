package nhncommerce.project.order

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.deliver.DeliverService
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.option.OptionService
import nhncommerce.project.order.domain.OrderDTO
import nhncommerce.project.order.domain.OrderRequestDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.product.ProductService
import nhncommerce.project.product.domain.ProductOptionDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse

@Controller
class OrderController (
    val orderService: OrderService,
    val couponService: CouponService,
    val deliverService: DeliverService,
    val optionService: OptionService,
    val loginInfoService: LoginInfoService
)

{
    /**
     * 상품 주문 페이지
     */
    @PostMapping("/orderProducts")
    fun orderProductPage(
        @RequestParam("optionDetailId") optionDetailId: Long,
        model: Model): String
    {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val orderRequestDTO = OrderRequestDTO(status = Status.ACTIVE,0,null,null,0,optionDetailId,0)
        val couponListViewDTO = couponService.getCouponViewList(loginInfo.userId)
        val deliverListviewDTO = deliverService.getDeliverViewList(loginInfo.userId)
        val optionDetailDTO = optionService.getOptionDetail(optionDetailId)

        model.addAttribute("optionDetailDTO", optionDetailDTO)
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
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderRequestDTO.userId = loginInfo.userId
        orderService.createOrder(orderRequestDTO, response)
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
    @GetMapping("/orders/myOrderList")
    fun orderList(pageRequestDTO: PageRequestDTO, model: Model):String{
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("orders",orderService.getMyOrderList(pageRequestDTO,loginInfo.userId))
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
    @GetMapping("/orders/{orderId}")
    fun getMyOrder(@PathVariable("orderId")orderId: Long, model: Model):String{
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("orderInfo",orderService.getOrder(orderId,loginInfo.userId))
        return "order/MyOrder"
    }

    /**
     * 나의 주문 취소
     */
    @PutMapping("/orders/orderCancel/{orderId}")
    fun cancelMyOrder(@PathVariable("orderId")orderId:Long, orderDTO:OrderDTO, response: HttpServletResponse): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderService.cancelMyOrder(orderDTO,orderId,loginInfo.userId,response)
        return "order/myOrderList"
    }
}