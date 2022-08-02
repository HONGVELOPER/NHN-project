package nhncommerce.project.user


import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.user.domain.UserDTO
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*


@Controller
class UserController(
    val userService: UserService
) {

    @GetMapping("/loginForm")
    fun loginForm(): String? {
        return "user/login"
    }

    @GetMapping("/joinForm")
    fun joinForm(userDto: UserDTO): String? {
        return "user/join"
    }

    @GetMapping("/updateForm")
    fun updateForm(userDTO: UserDTO, model: Model,  @AuthenticationPrincipal formLoginUserDetails: FormLoginUserDetails): String? {
        println(formLoginUserDetails)
        val userId: Long? = formLoginUserDetails.getId():
        val userDTO: UserDTO = userService.findUserById(userId)
//        so
        model.addAttribute(userDTO)
        println("update  : $userDTO")
        return "user/update"
    }

    @PostMapping("/users")
    fun createUserByForm(@ModelAttribute userDTO: UserDTO, bindingResult: BindingResult):  String {
        println("유저 회원가입 진입")
        println(userDTO.toString())
        userService.createUserByForm(userDTO)
        return "index"
    }

    @GetMapping("/users/me")
    fun findUserById(@AuthenticationPrincipal formLoginUserDetails: FormLoginUserDetails) {
        println("userId : ${formLoginUserDetails.getId()}")
        val userId: Long = formLoginUserDetails.getId()
        userService.findUserById(userId)
    }

    @PutMapping("user")
    fun updateUserById(@ModelAttribute userDTO: UserDTO, bindingResult: BindingResult, @AuthenticationPrincipal formLoginUserDetails: FormLoginUserDetails) {
        val userId: Long = formLoginUserDetails.getId()
        userService.updateUserById(userId, userDTO)
    }

    @DeleteMapping("user")
    fun deleteUserById(@AuthenticationPrincipal formLoginUserDetails: FormLoginUserDetails) {
        userService.deleteUserById(formLoginUserDetails.getId())
    }

    // 권한 확인 위한 테스트 api
    @GetMapping("/api/check")
    fun test(): String {
        println("api test 진입")
        val auth = SecurityContextHolder.getContext().authentication
        println("result : $auth")
        return "test"
    }

}