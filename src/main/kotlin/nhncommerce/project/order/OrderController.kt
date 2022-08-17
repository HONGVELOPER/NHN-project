package nhncommerce.project.order

import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.deliver.DeliverService
import nhncommerce.project.option.OptionService
import nhncommerce.project.order.domain.OrderRequestDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.user.UserService
import nhncommerce.project.util.alert.AlertService
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse

@Controller
class OrderController(
    val orderService: OrderService,
    val couponService: CouponService,
    val deliverService: DeliverService,
    val optionService: OptionService,
    val loginInfoService: LoginInfoService,
    val userService: UserService,
    val alertService: AlertService


) {
    /**
     * 상품 주문 페이지
     * */
//    @PostMapping("/api/orderProducts")
//    fun orderProductPage(
//        @RequestParam("optionDetailId") optionDetailId: Long? = null,
//        @RequestParam("productId") productId: String,
//        mav: ModelAndView
//    ): ModelAndView {
//        if (optionDetailId == null) {
//            mav.addObject("data", alertDTO("선택된 옵션이 없습니다.", "/products/" + productId))
//            mav.viewName = "user/alert"
//            return mav
//        }
//        val optionDetailDTO = optionService.getOptionDetail(optionDetailId)
//        couponService.updateCouponStatus()
//        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
//
//        val couponListViewDTO = couponService.getCouponViewList(loginInfo.userId)
//        val deliverListviewDTO = deliverService.getDeliverViewList(loginInfo.userId)
//        val userDTO = userService.findUserById(loginInfo.userId)
//        val orderRequestDTO = OrderRequestDTO(
//            status = Status.ACTIVE,
//            price = 0,
//            phone = userDTO.phone?: "",
//            userId = loginInfo.userId,
//            couponId = null,
//            optionDetailId = optionDetailId,
//            deliverId = null
//        )
//
//
//        mav.addObject("userDTO", userDTO)
//        mav.addObject("optionDetailDTO", optionDetailDTO)
//        mav.addObject("deliverListViewDTO", deliverListviewDTO)
//        mav.addObject("couponListViewDTO", couponListViewDTO)
//        mav.addObject("orderRequestDTO", orderRequestDTO)
//        mav.viewName = "order/orderProduct"
//        return mav
//    }
    @PostMapping("/api/orderProducts")
    fun orderProductPage(
        @RequestParam("optionDetailId") optionDetailId: Long,
        @RequestParam("productId") productId: String,
        mav: ModelAndView
    ): ModelAndView {
        val optionDetailDTO = optionService.getOptionDetail(optionDetailId)
        couponService.updateCouponStatus()
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()

        val couponListViewDTO = couponService.getCouponViewList(loginInfo.userId)
        val deliverListviewDTO = deliverService.getDeliverViewList(loginInfo.userId)
        val userDTO = userService.findUserById(loginInfo.userId)
        val orderRequestDTO = OrderRequestDTO(
            status = Status.ACTIVE,
            price = 0,
            phone = userDTO.phone?: "",
            userId = loginInfo.userId,
            couponId = null,
            optionDetailId = optionDetailId,
            deliverId = null
        )


        mav.addObject("userDTO", userDTO)
        mav.addObject("optionDetailDTO", optionDetailDTO)
        mav.addObject("deliverListViewDTO", deliverListviewDTO)
        mav.addObject("couponListViewDTO", couponListViewDTO)
        mav.addObject("orderRequestDTO", orderRequestDTO)
        mav.viewName = "order/orderProduct"
        return mav
    }

    /**
     * 상품 주문
     */
    @PostMapping("/api/orders")
    fun orderProduct(orderRequestDTO: OrderRequestDTO, response: HttpServletResponse) {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()


        orderService.createOrder(orderRequestDTO, loginInfo.userId)
        alertService.alertMessage("주문이 완료되었습니다.", "/user", response)
    }


    /**
     * 나의 주문내역 전체 조회 페이지 -user
     */
    @GetMapping("/api/orders")
    fun orderList(pageRequestDTO: PageRequestDTO, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("userOrders", orderService.getMyOrderList(pageRequestDTO, loginInfo.userId))
        return "order/orderList"
    }


    /**
     * 관라자용 모든 사용자 주문내역 전체 조회 페이지 -admin
     */
    @GetMapping("/admin/orders")
    fun orderListTest(pageRequestDTO: PageRequestDTO, model: Model): String {
        model.addAttribute("orders", orderService.getOrderList(pageRequestDTO))
        return "order/adminOnlyOrderList"
    }

    /**
     * 나의 주문내역 단건조회 - user
     */
    @GetMapping("/api/orders/{orderId}")
    fun getMyOrder(@PathVariable("orderId") orderId: Long, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("userOrderInfo", orderService.getUserOrder(orderId, loginInfo.userId))
        return "order/myOrderView"
    }

    /**
     * 관리자용 사용자 주문내역 단건조회 - admin
     */
    @GetMapping("/admin/orders/{orderId}")
    fun getOrder(@PathVariable("orderId") orderId: Long, model: Model): String {
        model.addAttribute("orderInfo", orderService.getOrder(orderId))
        return "order/adminOnlyOrderView"
    }


    /**
     * 주문 취소 - user
     */
    @PutMapping("/api/orders/orderCancel/{orderId}")
    fun cancelMyOrder(@PathVariable("orderId") orderId: Long, response: HttpServletResponse) {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderService.cancelMyOrder(orderId, loginInfo.userId)
        alertService.alertMessage("주문이 취소되었습니다.", "/api/orders", response)
    }

    /**
     * 주문 취소 - admin
     */
    @PutMapping("/admin/orders/orderCancel/{orderId}")
    fun calcelOrder(@PathVariable("orderId") orderId: Long, response: HttpServletResponse) {
        orderService.cancelOrder(orderId)
        alertService.alertMessage("주문이 취소되었습니다.", "/admin/orders", response)
    }

}