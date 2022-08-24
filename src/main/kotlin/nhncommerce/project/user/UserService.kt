package nhncommerce.project.user


import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.exception.AlertException
import nhncommerce.project.exception.ErrorMessage
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.review.ReviewRepository
import nhncommerce.project.user.domain.*
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Function

@Service
class UserService(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository,
    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {

    @Transactional
    fun createUserByForm(userDTO: UserDTO) {
       userRepository.findByEmail(userDTO.email)?.let {
            throw AlertException(ErrorMessage.DUPLICATE_EMAIL)
       }
        val user: User = userDTO.dtoToEntity()
        val encodedPassword = passwordEncoder.encode(user.password)
        user.updatePassword(encodedPassword)
        userRepository.save(user)
    }

    fun findUserById(userId: Long): UserDTO {
        val user: User = userRepository.findById(userId).get()
        println(user.toString())
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
    fun updateUserProfileByAdmin(userId: Long, adminProfileDTO: AdminProfileDTO): String {
        val user: User = userRepository.findById(userId).get()
        user.updateProfileByAdmin(adminProfileDTO)
        return user.email
    }


    @Transactional
    fun updateUserPasswordById(userId: Long, passwordDTO: PasswordDTO) {
        val user: User = userRepository.findById(userId).get()
        if (!passwordEncoder.matches(passwordDTO.password, user.password)) { // todo : controller에서 처리하든 fe에서 처리
            throw AlertException(ErrorMessage.INCORRECT_ORIGIN_PASSWORD)
        }
        val newEncodedPassword = passwordEncoder.encode(passwordDTO.newPassword)
        user.updatePassword(newEncodedPassword)
    }

    @Transactional
    fun deleteUserById(userId: Long): String {
        val user = userRepository.findById(userId).get() // todo: suchElementException -> controller advice 처리하자
        user.deleteUser()
        val reviewList = reviewRepository.findByUser(user)
        reviewList.map {
            it.status = Status.IN_ACTIVE
        }
        return user.email
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