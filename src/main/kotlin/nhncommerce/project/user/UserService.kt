package nhncommerce.project.user


import nhncommerce.project.user.domain.PasswordDTO
import nhncommerce.project.user.domain.User
import nhncommerce.project.user.domain.UserDTO
import nhncommerce.project.util.alert.AlertService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletResponse

@Service
class UserService(
    val userRepository: UserRepository,
    val alertService: AlertService,
    val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {

    fun createUserByForm(userDTO: UserDTO) : User {
        println("유저 서비스 ")
        val user: User = userDTO.toEntity()
        return userRepository.save(user)
    }

    fun findUserById(userId: Long): UserDTO {
        val user: User = userRepository.findById(userId).get()
        return UserDTO.fromEntity(user)
    }


    fun updateUserProfileById(userId: Long, userDTO: UserDTO) : User {
        println(userDTO.toString())
        val user: User = userRepository.findById(userId).get()
        user.updateProfile(userDTO)
        val updatedUser = userRepository.save(user)
        return updatedUser
    }

    fun updateUserPasswordById(userId: Long, passwordDTO: PasswordDTO, response: HttpServletResponse) {
        val user: User = userRepository.findById(userId).get()
        if (!passwordEncoder.matches(passwordDTO.password, user.password)) {
            alertService.alertMessage("기존 패스워드가 일치하지 않습니다.", "/user", response)
            return
        } else if (passwordDTO.password == passwordDTO.newPassword) {
            alertService.alertMessage("기존 패스워드와 일치하는 비밀번호는 수정할 수 없습니다.", "/user", response)
            return
        } else if (passwordDTO.newPassword != passwordDTO.newPasswordVerify) {
            alertService.alertMessage("새로운 비밀번호가 일치하지 않습니다.", "/user", response)
            return
        }
        val newEncodedPassword = passwordEncoder.encode(passwordDTO.newPassword)
        user.updatePassword(newEncodedPassword)
        userRepository.save(user)
    }

    fun deleteUserById(userId: Long) {
        val deleteUser = userRepository.findById(userId).get()
        userRepository.deleteById(deleteUser.userId!!)
    }


    private fun notFoundUser(response: HttpServletResponse) {
        alertService.alertMessage("존재하지 않는 회원 입니다. 다시 입력해주세요.","/publishCouponPage",response)
    }
}