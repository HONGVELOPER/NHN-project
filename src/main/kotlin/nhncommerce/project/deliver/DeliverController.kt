package nhncommerce.project.deliver

import nhncommerce.project.deliver.domain.Deliver
import nhncommerce.project.deliver.domain.DeliverDTO
import nhncommerce.project.deliver.domain.DeliverListDTO
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Controller
class DeliverController (
    val deliverService: DeliverService,
    val loginInfoService: LoginInfoService,
) {

    /*
    * 배송지 추가 페이지
    * */
    @GetMapping("deliverCreateForm")
    fun createDeliverForm(deliverDTO: DeliverDTO): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            return "deliver/create"
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

    /*
    * 배송지 수정 페이지
    */
    @GetMapping("deliverUpdateForm/{deliverId}")
    fun updateDeliverForm(
        @PathVariable("deliverId") deliverId: Long,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            val deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, loginInfo.userId)
            mav.addObject("deliverId", deliverId)
            mav.addObject("deliverDTO", deliverDTO)
            mav.viewName = "deliver/update"
            return mav
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

    /*
    * 배송지 추가
    * */
    @PostMapping("/delivers")
    fun createDeliver(
        @ModelAttribute deliverDTO: DeliverDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "deliver/create"
            return mav
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            deliverService.createDeliver(deliverDTO, loginInfo.userId)
            mav.addObject("data", alertDTO("배송지가 등록되었습니다.", "/delivers/users"))
            mav.viewName = "user/alert"
            return mav
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

    /*
    * 단일 배송지 조회
    * */
    @GetMapping("/delivers/{deliverId}")
    fun findDeliverById(@PathVariable("deliverId") deliverId: Long) {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            val deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, loginInfo.userId)
            println(deliverDTO.toString())
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

    /*
    * 유저 배송지 목록 조회
    * */
    @GetMapping("/delivers/users")
    fun findDeliverByUser(pageRequestDTO: PageRequestDTO, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            val result: PageResultDTO<DeliverListDTO, Deliver> = deliverService.findDeliverListByUser(loginInfo.userId, pageRequestDTO)
            mav.addObject("delivers", result)
            mav.viewName = "deliver/deliverList"
            return mav
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

   /*
    * 배송지 수정
    * */
    @PutMapping("/delivers/{deliverId}")
    fun updateDeliver(
        @PathVariable("deliverId") deliverId: Long,
        @ModelAttribute deliverDTO: DeliverDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "deliver/update"
            return mav
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            deliverService.updateDeliver(loginInfo.userId, deliverId, deliverDTO)
            mav.addObject("data", alertDTO("배송지가 정상적으로 수정되었습니다.", "/delivers/users"))
            mav.viewName = "user/alert"
            return mav
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }

    /*
    * 배송지 삭제
    * */
    @DeleteMapping("/delivers/{deliverId}")
    fun deleteDeliverById(
        @PathVariable("deliverId") deliverId: Long,
        mav: ModelAndView
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            deliverService.deleteDeliverById(loginInfo.userId, deliverId)
            mav.addObject("data", alertDTO("배송지가 정상적으로 삭제되었습니다.", "/delivers/users"))
            mav.viewName = "user/alert"
            return mav
        } else {
            throw RedirectException(alertDTO("로그인이 필요한 서비스입니다.", "/login"))
        }
    }
}