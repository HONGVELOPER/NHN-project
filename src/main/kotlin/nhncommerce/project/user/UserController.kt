package nhncommerce.project.user

import nhncommerce.project.security.domain.FormLoginUserDetails
import nhncommerce.project.security.domain.Oauth2LoginUserDetails
import nhncommerce.project.user.domain.PasswordDTO
import nhncommerce.project.user.domain.UserDTO
import nhncommerce.project.util.alert.AlertService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse


@Controller
class UserController(
    val userService: UserService,
    val alertService: AlertService,
) {

    @GetMapping("/user")
    fun main():String{
        return "user/index"
    }

    @GetMapping("/loginForm")
    fun loginForm(): String {
        return "user/login"
    }

    @GetMapping("/joinForm")
    fun joinForm(userDto: UserDTO): String {
        return "user/join"
    }

    @GetMapping("/updateProfileForm")
    fun updateProfileForm(
        model: Model,
    ): String {
        val userId: Long = getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(userId)
        model.addAttribute("userDTO", userDTO)
        return "user/updateProfile"
    }

    @GetMapping("/updatePasswordForm")
    fun updatePasswordForm(passwordDTO: PasswordDTO, response: HttpServletResponse): String {
        val userId: Long = getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(userId)
        if (userDTO.provider != "") {
            alertService.alertMessage("소셜 로그인 유저는 비밀번호를 변경할 수 없습니다.", "/admin", response)
        }
        println("안찍히지~~~~~~~~~~~~~~~")
        return "user/updatePassword"
    }

    @PostMapping("/users")
    fun createUserByForm(@ModelAttribute userDTO: UserDTO, bindingResult: BindingResult):  String {
        println("유저 회원가입 진입")
        println(userDTO.toString())
        userService.createUserByForm(userDTO)
        return "user/index"
    }

    @GetMapping("/users/me")
    fun findUserById() {
        val userId: Long = getUserIdFromSession()
        userService.findUserById(userId)
    }

    @PutMapping("/users/profile")
    fun updateUserProfileById(
        @ModelAttribute userDTO: UserDTO,
        bindingResult: BindingResult,
    ) {
        val userId: Long = getUserIdFromSession()
        userService.updateUserProfileById(userId, userDTO)
    }

    @PutMapping("/users/password")
    fun updateUserPasswordById(
        @ModelAttribute passwordDTO: PasswordDTO,
        bindingResult: BindingResult,
        response: HttpServletResponse
    ) {
        val userId: Long = getUserIdFromSession()
        println("password dto : ${passwordDTO.toString()}")
        userService.updateUserPasswordById(userId, passwordDTO, response)
    }

    @DeleteMapping("/users")
    fun deleteUserById() {
        val userId: Long = getUserIdFromSession()
        userService.deleteUserById(userId)
    }

    // 권한 확인 위한 테스트 api
    @GetMapping("/api/test")
    fun test(): String {
        println("api test 진입")
        val userId: Long = getUserIdFromSession()
        println("user id : $userId")
        return "redirect:/user"
    }

    fun getUserIdFromSession(): Long {
        val userId: Long
        val auth = SecurityContextHolder.getContext().authentication.principal
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