package nhncommerce.project.deliver

import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import nhncommerce.project.deliver.domain.DeliverDTO
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@Controller
class DeliverController (
    val deliverService: DeliverService
) {
    @GetMapping("deliverCreateForm")
    fun createDeliverForm(deliverDTO: DeliverDTO): String {
        return "deliver/create"
    }

    @GetMapping("deliverUpdateForm/{deliverId}")
    fun updateDeliverForm(
        @PathVariable("deliverId") deliverId: Long,
        model: Model,
        response: HttpServletResponse
    ): String {
        var userId: Long = getUserIdFromSession()
        var deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, userId, response)
        model.addAttribute("deliverId", deliverId)
        model.addAttribute("deliverDTO", deliverDTO)
        return "deliver/update"
    }

    @PostMapping("/delivers")
    fun createDeliver(@ModelAttribute deliverDTO: DeliverDTO, bindingResult: BindingResult) {
        println("배송지 추가 진입")
        println("deliver dto : ${deliverDTO.toString()}")
        val userId: Long = getUserIdFromSession()
        deliverService.createDeliver(deliverDTO, userId)
    }

    @GetMapping("/delivers/{deliverId}")
    fun findDeliverById(@PathVariable("deliverId") deliverId: Long, response: HttpServletResponse) {
        val userId: Long = getUserIdFromSession()
        val deliverDTO: DeliverDTO = deliverService.findDeliverById(deliverId, userId, response)
        println(deliverDTO.toString())
    }

    @PutMapping("/delivers/{deliverId}")
    fun updateDeliver(
        @PathVariable("deliverId") deliverId: Long,
        @ModelAttribute deliverDTO: DeliverDTO,
        bindingResult: BindingResult,
        response: HttpServletResponse
    ) {
        println("deliver update 진입~~~~~~~~~~~")
        println("update : ${deliverDTO.toString()}")
        val userId: Long = getUserIdFromSession()
        deliverService.updateDeliver(userId, deliverId, deliverDTO,  response)
    }

    fun getUserIdFromSession(): Long {
        val userId: Long
        val auth = SecurityContextHolder.getContext().authentication.principal
        println("auth : $auth")
//        if (auth == "anonymousUser") { 로그인 안한 유저 예외처리 해야해.
//
//        }
        val loginStatus = auth.javaClass.toString().split(".")[4]
        if (loginStatus == "FormLoginUserDetails") {
            val formLoginUserDetails: FormLoginUserDetails = auth as FormLoginUserDetails
            userId = formLoginUserDetails.getId()
        } else {
            val oAuth2LoginUserDetails: Oauth2LoginUserDetails = auth as Oauth2LoginUserDetails
            userId = oAuth2LoginUserDetails.getId()
        }
        return userId
    }

}