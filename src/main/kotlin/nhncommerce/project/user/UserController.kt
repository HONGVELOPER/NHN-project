package nhncommerce.project.user

import nhncommerce.project.user.domain.PasswordDTO
import nhncommerce.project.user.domain.ProfileDTO
import nhncommerce.project.user.domain.UserDTO
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.validation.Valid


@Controller
class UserController(
    val userService: UserService,
    val loginInfoService: LoginInfoService,
) {

    @GetMapping("/user")
    fun userForm():String{
        return "user/index"
    }
    
    @GetMapping("/loginForm")
    fun loginForm(): String {
        return "user/login"
    }

    /*
    * 회원 가입 페이지
    * */
    @GetMapping("/joinForm")
    fun joinForm(userDto: UserDTO): String {
        return "user/join"
    }

    /*
    * 회원 프로필 수정 페이지
    * */
    @GetMapping("/updateProfileForm")
    fun updateProfileForm(
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            val profileDTO: ProfileDTO = userService.findUserProfileById(loginInfo.userId)
            mav.addObject("profileDTO", profileDTO)
            mav.viewName = "user/updateProfile"
        } else {
            mav.addObject("data", alertDTO("로그인이 필요한 서비스입니다.", "/login"))
            mav.viewName = "user/alert"
        }
        return mav
    }

    /*
    * 회원 비밀번호 수정 페이지
    * */
    @GetMapping("/updatePasswordForm")
    fun updatePasswordForm(
        passwordDTO: PasswordDTO,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin) {
            val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
            if (userDTO.provider != "") {
                mav.addObject("data", alertDTO("소셜 로그인 유저는 비밀번호를 변경할 수 없습니다.", "/user"))
                mav.viewName = "user/alert"
                return mav
            }
            mav.viewName = "user/updatePassword"
        } else {
            mav.addObject("data", alertDTO("로그인이 필요한 서비스입니다.", "/login"))
            mav.viewName = "user/alert"
        }
        return mav
    }

    @PostMapping("/users")
    fun createUserByForm(
        @Valid @ModelAttribute userDTO: UserDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/join"
            return mav
        }
        userService.createUserByForm(userDTO)
        mav.addObject("data", alertDTO("회원 가입이 완료되었습니다.", "/user"))
        mav.viewName = "user/alert"
        return mav
    }

    @GetMapping("/users/me")
    fun findUserById(): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.findUserById(loginInfo.userId)
        return "user/index"
    }

    @PutMapping("/users/profile")
    fun updateUserProfileById(
        @Valid @ModelAttribute profileDTO: ProfileDTO,
        bindingResult: BindingResult,
        mav: ModelAndView,
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/updateProfile"
            return mav
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.updateUserProfileById(loginInfo.userId, profileDTO)
        mav.addObject("data", alertDTO("회원 프로필이 정상적으로 수정되었습니다.", "/user"))
        mav.viewName = "user/alert"
        return mav
    }

    @PutMapping("/users/password")
    fun updateUserPasswordById(
        @Valid @ModelAttribute passwordDTO: PasswordDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ):  ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/updatePassword"
            return mav
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.updateUserPasswordById(loginInfo.userId, passwordDTO)
        mav.addObject("data", alertDTO("회원 비밀번호가 정상적으로 수정되었습니다.", "/user"))
        mav.viewName = "user/alert"
        return mav
    }

    @DeleteMapping("/users")
    fun deleteUserById(mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.deleteUserById(loginInfo.userId)
        mav.addObject("data", alertDTO("회원 탈퇴가 완료되었습니다.", "/user"))
        mav.viewName = "user/alert"
        return mav
    }

//     권한 확인 위한 테스트 api
    @GetMapping("/api/test")
    fun test(): String {
        println("api test 진입")
        return "redirect:/user"
    }

    @GetMapping("/admin/test")
    fun adminTest(): String {
        println("admin test 진입")
        return "redirect:/user"
    }

}