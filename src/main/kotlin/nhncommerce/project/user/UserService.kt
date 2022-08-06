package nhncommerce.project.user


import nhncommerce.project.user.domain.PasswordDTO
import nhncommerce.project.user.domain.User
import nhncommerce.project.user.domain.UserDTO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(

    val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
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


    fun updateUserProfileById(userId: Long, userDTO: UserDTO) : User {
        println(userDTO.toString())
        val user: User = userRepository.findById(userId).get()
        user.updateProfile(userDTO)
        val updatedEntity = userRepository.save(user)
        return updatedEntity
    }

    fun updateUserPasswordById(userId: Long, passwordDTO: PasswordDTO): User {
        val user: User = userRepository.findById(userId).get()
        if (!passwordEncoder.matches(passwordDTO.password, user.password)) {
            println("비밀번호 매칭이 안되서 끊겨야함. 유틸 가져와서 밀어낼게요")
        } else if (passwordDTO.newPassword != passwordDTO.newPasswordVerify) {
            println("바꾸려는 비밀번호 일치하지 않아요, 끊겨야함")
        }
        val newEncodedPassword = passwordEncoder.encode(passwordDTO.newPassword)
        user.updatePassword(newEncodedPassword)
        val updatedEntity = userRepository.save(user)
        return updatedEntity
    }

    fun deleteUserById(userId: Long) {

        val deleteUser: User = userRepository.findById(userId).get()
        userRepository.deleteById(userId)
    }


//    private fun notFoundUser(response: HttpServletResponse) {
//        alertService.alertMessage("존재하지 않는 회원 입니다. 다시 입력해주세요.","/publishCouponPage",response)
//    }
}