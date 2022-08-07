package nhncommerce.project.user


import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.RedirectException
import nhncommerce.project.user.domain.*
import nhncommerce.project.util.alert.alertDTO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {

    fun createUserByForm(userDTO: UserDTO) {
        val duplicateUser: User? = userRepository.findByEmail(userDTO.email)
        if (duplicateUser?.email == userDTO.email) {
            throw RedirectException(alertDTO("이미 존재하는 아이디 입니다", "/users/joinForm"))
        } else if (userDTO.password != userDTO.passwordVerify) {
            throw RedirectException(alertDTO("비밀번호가 일치하지 않습니다.", "/users/joinForm"))
        }
        val user: User = userDTO.toEntity()
        user.password = passwordEncoder.encode(user.password)
        userRepository.save(user)
    }

    fun findUserById(userId: Long): UserDTO {
        val user: User = userRepository.findById(userId).get()
        return UserDTO.fromEntity(user)
    }

    fun findUserProfileById(userId: Long): ProfileDTO {
        val user: User = userRepository.findById(userId).get()
        return ProfileDTO.fromEntity(user)
    }

    fun updateUserProfileById(userId: Long, profileDTO: ProfileDTO) {
        val user: User = userRepository.findById(userId).get()
        user.updateProfile(profileDTO)
        userRepository.save(user)
    }

    fun updateUserPasswordById(userId: Long, passwordDTO: PasswordDTO) {
        val user: User = userRepository.findById(userId).get()
        if (!passwordEncoder.matches(passwordDTO.password, user.password)) {
            throw RedirectException(alertDTO("비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"))
        } else if (passwordDTO.password == passwordDTO.newPassword) {
            throw RedirectException(alertDTO("기존 비밀번호와 일치하는 비밀번호로 수정할 수 없습니다.", "/api/users/updatePasswordForm"))
        } else if (passwordDTO.newPassword != passwordDTO.newPasswordVerify) {
            throw RedirectException(alertDTO("새로운 비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"))
        }
        val newEncodedPassword = passwordEncoder.encode(passwordDTO.newPassword)
        user.updatePassword(newEncodedPassword)
        userRepository.save(user)
    }

    fun deleteUserById(userId: Long) {
        val deleteUser = userRepository.findById(userId).get()
        userRepository.deleteById(deleteUser.userId!!)
    }
}