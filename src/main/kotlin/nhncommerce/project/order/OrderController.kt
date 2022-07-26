package nhncommerce.project.order

import com.querydsl.core.types.dsl.Wildcard.count
import nhncommerce.project.baseentity.Status
import nhncommerce.project.coupon.CouponService
import nhncommerce.project.deliver.DeliverService
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.option.OptionService
import nhncommerce.project.order.domain.OrderRequestDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.user.UserService
import nhncommerce.project.user.domain.UserDTO
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
    val userService: UserService


) {

    @GetMapping("/api/orderProducts")
    fun orderProductPage(
        @RequestParam("optionDetailId") optionDetailId: Long,
        @RequestParam("productId") productId: String,
        @RequestParam("count") count: Int,
        mav: ModelAndView
    ): ModelAndView {
        val optionDetailDTO = optionService.getOptionDetail(optionDetailId)
        couponService.updateCouponStatus()
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()

        val couponListViewDTO = couponService.getCouponViewList(loginInfo.userId)
        val deliverListviewDTO = deliverService.getDeliverViewList(loginInfo.userId)
        val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
        val orderRequestDTO = OrderRequestDTO(
            status = Status.ACTIVE,
            price = 0,
            phone = userDTO.phone.orEmpty(),
            userId = loginInfo.userId,
            couponId = null,
            optionDetailId = optionDetailId,
            count = count,
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


    @PostMapping("/api/orders")
    fun orderProduct(orderRequestDTO: OrderRequestDTO, mav: ModelAndView): ModelAndView {
        if (orderRequestDTO.deliverId == null) {
            throw AlertException(ErrorMessage.NOT_EXIST_ADDRESS)
        }
        if (orderRequestDTO.phone == "") {
            throw AlertException(ErrorMessage.NOT_EXIST_USER_PHONE)
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderService.createOrder(orderRequestDTO, loginInfo.userId)
        mav.addObject("data", alertDTO("주문이 완료되었습니다.", "/api/orders"))
        mav.viewName = "user/alert"
        return mav

    }


    @GetMapping("/api/orders")
    fun orderList(pageRequestDTO: PageRequestDTO, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("userOrders", orderService.getUserOrderList(pageRequestDTO, loginInfo.userId))
        model.addAttribute("type",pageRequestDTO.type)
        model.addAttribute("keyword",pageRequestDTO.keyword)
        return "order/orderList"
    }


    @GetMapping("/admin/orders")
    fun orderAdminListTest(pageRequestDTO: PageRequestDTO, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("orders", orderService.getAdminOrderList(pageRequestDTO,loginInfo.userId))
        model.addAttribute("type",pageRequestDTO.type)
        model.addAttribute("keyword",pageRequestDTO.keyword)
        return "order/adminOnlyOrderList"
    }


    @GetMapping("/api/orders/{orderId}")
    fun getMyOrder(@PathVariable("orderId") orderId: Long, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("userOrderInfo", orderService.getUserOrder(orderId, loginInfo.userId))
        return "order/myOrderView"
    }


    @GetMapping("/admin/orders/{orderId}")
    fun getAdminOrder(@PathVariable("orderId") orderId: Long, model: Model): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        model.addAttribute("orderInfo", orderService.getAdminOrder(orderId,loginInfo.userId))
        return "order/adminOnlyOrderView"
    }


    @PutMapping("/api/orders/orderCancel/{orderId}")
    fun cancelMyOrder(@PathVariable("orderId") orderId: Long, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderService.cancelOrder(orderId, loginInfo.userId)
        mav.addObject("data", alertDTO("주문이 취소되었습니다.", "/api/orders"))
        mav.viewName = "user/alert"
        return mav
    }


    @PutMapping("/admin/orders/orderCancel/{orderId}")
    fun cancelOrder(@PathVariable("orderId") orderId: Long, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        orderService.cancelOrder(orderId, loginInfo.userId)
        mav.addObject("data", alertDTO("주문이 취소되었습니다.", "/admin/orders"))
        mav.viewName = "user/alert"
        return mav
    }

}