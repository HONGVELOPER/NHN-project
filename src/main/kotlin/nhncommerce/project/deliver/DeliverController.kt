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
    @GetMapping("/api/delivers/createForm")
    fun createDeliverForm(deliverDTO: DeliverDTO): String {
        return "deliver/create"
    }

    /*
    * 배송지 수정 페이지
    */
    @GetMapping("/api/delivers/updateForm/{deliverId}")
    fun updateDeliverForm(
        @PathVariable("deliverId") deliverId: Long,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, loginInfo.userId)
        mav.addObject("deliverId", deliverId)
        mav.addObject("deliverDTO", deliverDTO)
        mav.viewName = "deliver/update"
        return mav
    }

    /*
    * 배송지 추가
    * */
    @PostMapping("/api/delivers")
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
        deliverService.createDeliver(deliverDTO, loginInfo.userId)
        mav.addObject("data", alertDTO("배송지가 등록되었습니다.", "/api/delivers/users"))
        mav.viewName = "user/alert"
        return mav
    }

    /*
    * 단일 배송지 조회
    * */
    @GetMapping("/api/delivers/{deliverId}")
    fun findDeliverById(@PathVariable("deliverId") deliverId: Long) {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, loginInfo.userId)
        println(deliverDTO.toString())
    }

    /*
    * 유저 배송지 목록 조회
    * */
    @GetMapping("/api/delivers/users")
    fun findDeliverByUser(pageRequestDTO: PageRequestDTO, mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val result: PageResultDTO<DeliverListDTO, Deliver> = deliverService.findDeliverListByUser(loginInfo.userId, pageRequestDTO)
        mav.addObject("delivers", result)
        mav.viewName = "deliver/deliverList"
        return mav
    }

   /*
    * 배송지 수정
    * */
    @PutMapping("/api/delivers/{deliverId}")
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
        deliverService.updateDeliver(loginInfo.userId, deliverId, deliverDTO)
        mav.addObject("data", alertDTO("배송지가 정상적으로 수정되었습니다.", "/api/delivers/users"))
        mav.viewName = "user/alert"
        return mav
    }

    /*
    * 배송지 삭제
    * */
    @DeleteMapping("/api/delivers/{deliverId}")
    fun deleteDeliverById(
        @PathVariable("deliverId") deliverId: Long,
        mav: ModelAndView
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        deliverService.deleteDeliverById(loginInfo.userId, deliverId)
        mav.addObject("data", alertDTO("배송지가 정상적으로 삭제되었습니다.", "/api/delivers/users"))
        mav.viewName = "user/alert"
        return mav
    }
}