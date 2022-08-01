package nhncommerce.project.user

import nhncommerce.project.user.domain.User
import nhncommerce.project.user.domain.UserDTO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
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

    fun updateUserById(userId: Long, userDTO: UserDTO) : User {
        val user: User = userRepository.findById(userId).get()
        user.update(userDTO)
        val updatedEntity = userRepository.save(user)
        return updatedEntity
    }

    fun deleteUserById(userId: Long) {
        userRepository.deleteById(userId)
    }


//    private fun notFoundUser(response: HttpServletResponse) {
//        alertService.alertMessage("존재하지 않는 회원 입니다. 다시 입력해주세요.","/publishCouponPage",response)
//    }
}