package nhncommerce.project.user

import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.domain.*
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession
import javax.validation.Valid


@Controller
class UserController(
    private val userService: UserService,
    private val loginInfoService: LoginInfoService,
) {

    @GetMapping("/users/joinForm")
    fun joinForm(userDto: UserDTO, session: HttpSession): String {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        if (loginInfo.isLogin)
            return "redirect:/products"
        return "user/join"
    }

    @GetMapping("/api/users/myPageForm")
    fun myPageForm(mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
        mav.addObject("userDTO", userDTO)
        mav.viewName = "user/myPage"
        return mav
    }

    @GetMapping("/api/users/updateProfileForm")
    fun updateProfileForm(
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val profileDTO: ProfileDTO = userService.findUserProfileById(loginInfo.userId)
        mav.addObject("profileDTO", profileDTO)
        mav.viewName = "user/updateProfile"
        return mav
    }

    @GetMapping("/admin/users/{userId}/updateProfileForm")
    fun updateProfileFormByAdmin(
        @PathVariable("userId") userId: Long,
        mav: ModelAndView,
    ): ModelAndView {
        val adminProfileDTO: AdminProfileDTO = userService.findUserProfileByAdmin(userId)
        mav.addObject("adminProfileDTO", adminProfileDTO)
        mav.viewName = "user/updateProfileByAdmin"
        return mav
    }

    @GetMapping("/api/users/updatePasswordForm")
    fun updatePasswordForm(
        passwordDTO: PasswordDTO,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
        userDTO.provider?.let {
            mav.addObject("data", alertDTO("소셜 로그인 유저는 비밀번호를 변경할 수 없습니다.", "/products"))
            mav.viewName = "user/alert"
            return mav
        }
        mav.viewName = "user/updatePassword"
        return mav
    }

    @GetMapping("/api/users/sessionExpired")
    fun sessionExpired(
        mav: ModelAndView
    ): ModelAndView {
        mav.addObject("data", alertDTO("세션이 만료 되어 로그아웃 되었습니다. 다시 로그인 해주세요.", "/login"))
        mav.viewName = "user/alert"
        return mav
    }

    @GetMapping("/admin/users/manage")
    fun userManageForm(
        pageRequestDTO: PageRequestDTO,
        mav: ModelAndView
    ): ModelAndView {
        val result: PageResultDTO<UserListDTO, User> = userService.findUserList(pageRequestDTO)
        mav.addObject("users", result)
        mav.addObject("type",pageRequestDTO.type)
        mav.addObject("keyword",pageRequestDTO.keyword)
        mav.viewName = "user/userListByAdmin"
        return mav
    }

    @PostMapping("/users")
    fun createUser(
        @Valid @ModelAttribute userDTO: UserDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/join"
            return mav
        } else if (userDTO.password != userDTO.passwordVerify) { // todo : controller 에서 validation 해라
            throw AlertException(ErrorMessage.INCORRECT_PASSWORD)
        }
        println("user 컨트롤러 진입")
        try {
            userService.createUserByForm(userDTO)
            mav.addObject("data", alertDTO("회원 가입이 완료되었습니다.", "/products"))
            mav.viewName = "user/alert"
            return mav
        } catch(e: Exception) {
            println("에러 발생1!!!!!!!!!")
            throw Exception("error1")
        }
    }

    @PutMapping("/api/users/profile")
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
        mav.addObject("data", alertDTO("회원 프로필이 정상적으로 수정되었습니다.", "/api/users/myPageForm"))
        mav.viewName = "user/alert"
        return mav
    }

    @PutMapping("/admin/users/{userId}/profile")
    fun updateUserProfileByAdmin(
        @PathVariable("userId") userId: Long,
        @Valid @ModelAttribute adminProfileDTO: AdminProfileDTO,
        bindingResult: BindingResult,
        mav: ModelAndView,
    ): ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/updateProfileByAdmin"
            return mav
        }
        val userEmail = userService.updateUserProfileByAdmin(userId, adminProfileDTO)
        loginInfoService.expireUserSession(userEmail)
        mav.addObject("data", alertDTO("회원 프로필이 정상적으로 수정되었습니다.", "/admin/users/manage"))
        mav.viewName = "user/alert"
        return  mav
    }

    @PutMapping("/api/users/password")
    fun updateUserPasswordById(
        @Valid @ModelAttribute passwordDTO: PasswordDTO,
        bindingResult: BindingResult,
        mav: ModelAndView
    ):  ModelAndView {
        if (bindingResult.hasErrors()) {
            mav.viewName = "user/updatePassword"
            return mav
        } else if (passwordDTO.password == passwordDTO.newPassword) {
            throw AlertException(ErrorMessage.CHANGE_TO_ORIGIN_PASSWORD)
        } else if (passwordDTO.newPassword != passwordDTO.newPasswordVerify) {
            throw AlertException(ErrorMessage.INCORRECT_NEW_PASSWORD) //
        }
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.updateUserPasswordById(loginInfo.userId, passwordDTO)
        mav.addObject("data", alertDTO("회원 비밀번호가 정상적으로 수정되었습니다.", "/api/users/myPageForm"))
        mav.viewName = "user/alert"
        return mav
    }

    @DeleteMapping("/api/users")
    fun deleteUserById(mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val userEmail = userService.deleteUserById(loginInfo.userId)
        loginInfoService.expireUserSession(userEmail)
        mav.addObject("data", alertDTO("회원 탈퇴가 완료되었습니다.", "/products"))
        mav.viewName = "user/alert"
        return mav
    }

    @DeleteMapping("/admin/users/{userId}")
    fun deleteUserByAdmin(
        @PathVariable("userId") userId: Long,
        mav: ModelAndView
    ): ModelAndView {
        val userEmail = userService.deleteUserById(userId)
        loginInfoService.expireUserSession(userEmail)
        mav.addObject("data", alertDTO("회원 삭제가 완료되었습니다.", "/admin/users/manage"))
        mav.viewName = "user/alert"
        return mav
    }
}