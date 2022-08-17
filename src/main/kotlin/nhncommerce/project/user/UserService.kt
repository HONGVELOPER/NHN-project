package nhncommerce.project.user


import com.querydsl.core.BooleanBuilder
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.user.domain.*
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {

    @Transactional
    fun createUserByForm(userDTO: UserDTO) {
        val duplicateUser: User? = userRepository.findByEmail(userDTO.email)
        if (duplicateUser?.email == userDTO.email) {
            throw AlertException(ErrorMessage.DUPLICATE_EMAIL)
        } else if (userDTO.password != userDTO.passwordVerify) {
            throw AlertException(ErrorMessage.INCORRECT_PASSWORD)
        }
        val user: User = userDTO.dtoToEntity()
        val encodedPassword = passwordEncoder.encode(user.password)
        user.updatePassword(encodedPassword)
        userRepository.save(user)
    }

    fun findUserById(userId: Long): UserDTO {
        val user: User = userRepository.findById(userId).get()
        return user.entityToUserDto()
    }

    fun findUserProfileById(userId: Long): ProfileDTO {
        val user: User = userRepository.findById(userId).get()
        return user.entityToProfileDto()
    }

    fun findUserProfileByAdmin(userId: Long): AdminProfileDTO {
        val user: User = userRepository.findById(userId).get()
        return user.entityToAdminProfileDto()
    }

    @Transactional
    fun updateUserProfileById(userId: Long, profileDTO: ProfileDTO) {
        val user: User = userRepository.findById(userId).get()
        user.updateProfile(profileDTO)
    }

    @Transactional
    fun updateUserProfileByAdmin(userId: Long, adminProfileDTO: AdminProfileDTO) {
        val user: User = userRepository.findById(userId).get()
        user.updateProfileByAdmin(adminProfileDTO)
    }


    @Transactional
    fun updateUserPasswordById(userId: Long, passwordDTO: PasswordDTO) {
        val user: User = userRepository.findById(userId).get()
        if (!passwordEncoder.matches(passwordDTO.password, user.password)) {
            throw AlertException(ErrorMessage.INCORRECT_ORIGIN_PASSWORD)
        } else if (passwordDTO.password == passwordDTO.newPassword) {
            throw AlertException(ErrorMessage.CHANGE_TO_ORIGIN_PASSWORD)
        } else if (passwordDTO.newPassword != passwordDTO.newPasswordVerify) {
            throw AlertException(ErrorMessage.INCORRECT_NEW_PASSWORD)
        }
        val newEncodedPassword = passwordEncoder.encode(passwordDTO.newPassword)
        user.updatePassword(newEncodedPassword)
    }

    @Transactional
    fun deleteUserById(userId: Long) {
        val deleteUser = userRepository.findById(userId).get()
        userRepository.deleteById(deleteUser.userId)
    }

    fun findUserList(pageRequestDTO: PageRequestDTO): PageResultDTO<UserListDTO, User> {
        val booleanBuilder: BooleanBuilder = userListBuilder(pageRequestDTO)
        val pageable = pageRequestDTO.getPageable(Sort.by("userId").descending())
        val result = userRepository.findAll(booleanBuilder, pageable)
        val fn: Function<User, UserListDTO> =
            Function<User, UserListDTO> { entity: User? -> entity?.entityToUserListDto()}
        return PageResultDTO<UserListDTO, User>(result, fn)
    }


    fun userListBuilder(pageRequestDTO: PageRequestDTO): BooleanBuilder {
        val type = pageRequestDTO.type

        val booleanBuilder = BooleanBuilder()

        val qUser = QUser.user

        val keyword = pageRequestDTO.keyword

        val expression = qUser.userId.gt(1)

        booleanBuilder.and(expression)

        if(type.trim().isEmpty()){
            return booleanBuilder
        }

        val conditionBuilder = BooleanBuilder()

        if(type.contains("name")){
            conditionBuilder.or(qUser.name.contains(keyword))
        }
        if(type.contains("email")){
            conditionBuilder.or(qUser.email.contains(keyword))
        }

        booleanBuilder.and(conditionBuilder)

        return booleanBuilder
    }
}