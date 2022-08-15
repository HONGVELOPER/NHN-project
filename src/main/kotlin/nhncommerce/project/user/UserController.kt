package nhncommerce.project.user

import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.ProductService
import nhncommerce.project.user.domain.*
import nhncommerce.project.util.alert.alertDTO
import nhncommerce.project.util.loginInfo.LoginInfoDTO
import nhncommerce.project.util.loginInfo.LoginInfoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.validation.Valid


@Controller
class UserController(
    val userService: UserService,
    val productService: ProductService,
    val loginInfoService: LoginInfoService,
) {

    /*
    * 유저 메인 페이지
    * */
    @GetMapping("/user")
    fun userForm(model : Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/userProductList"
    }

    /*
    * 회원 가입 페이지
    * */
    @GetMapping("/users/joinForm")
    fun joinForm(userDto: UserDTO): String {
        return "user/join"
    }

    /*
    * 마이 페이지
    * */
    @GetMapping("/api/users/myPageForm")
    fun myPageForm(mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
        mav.addObject("userDTO", userDTO)
        mav.viewName = "user/myPage"
        return mav
    }

    /*
    * 회원 프로필 수정 페이지
    * */
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

    /*
    * 관리자 - 회원 프로필 수정 페이지
    * */
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

    /*
    * 회원 비밀번호 수정 페이지
    * */
    @GetMapping("/api/users/updatePasswordForm")
    fun updatePasswordForm(
        passwordDTO: PasswordDTO,
        mav: ModelAndView,
    ): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        val userDTO: UserDTO = userService.findUserById(loginInfo.userId)
        if (userDTO.provider != "") {
            mav.addObject("data", alertDTO("소셜 로그인 유저는 비밀번호를 변경할 수 없습니다.", "/user"))
            mav.viewName = "user/alert"
            return mav
        }
        mav.viewName = "user/updatePassword"
        return mav
    }

    /*
    * 관리자 - 회원 관리 페이지
\    * */
    @GetMapping("/admin/users/manage")
    fun userManageForm(
        pageRequestDTO: PageRequestDTO,
        mav: ModelAndView
    ): ModelAndView {
        val result: PageResultDTO<UserListDTO, User> = userService.findUserList(pageRequestDTO)
        mav.addObject("users", result)
        mav.viewName = "user/userListByAdmin"
        return mav
    }

    /*
    * 회원가입
    * */
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

    /*
    * 회원 프로필 수정
    * */
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

    /*
    * 관리자 - 회원 프로필 수정
    * */
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
        userService.updateUserProfileByAdmin(userId, adminProfileDTO)
        mav.addObject("data", alertDTO("회원 프로필이 정상적으로 수정되었습니다.", "/admin/users/manage"))
        mav.viewName = "user/alert"
        return mav
    }

    /*
    * 회원 비밀번호 수정
    * */
    @PutMapping("/api/users/password")
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
        mav.addObject("data", alertDTO("회원 비밀번호가 정상적으로 수정되었습니다.", "/api/users/myPageForm"))
        mav.viewName = "user/alert"
        return mav
    }

    /*
    * 회원 탈퇴
    * */
    @DeleteMapping("/api/users")
    fun deleteUserById(mav: ModelAndView): ModelAndView {
        val loginInfo: LoginInfoDTO = loginInfoService.getUserIdFromSession()
        userService.deleteUserById(loginInfo.userId)
        mav.addObject("data", alertDTO("회원 탈퇴가 완료되었습니다.", "/user"))
        mav.viewName = "user/alert"
        return mav
    }

    /*
    * 관리자 회원 삭제
    * */
    @DeleteMapping("/admin/users/{userId}")
    fun deleteUserById(
        @PathVariable("userId") userId: Long,
        mav: ModelAndView
    ): ModelAndView {
        userService.deleteUserById(userId)
        mav.addObject("data", alertDTO("회원 삭제가 완료되었습니다.", "/admin/users/manage"))
        mav.viewName = "user/alert"
        return mav
    }

}