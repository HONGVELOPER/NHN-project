package nhncommerce.project.login.domain

data class LoginDTO (
    var id: Long? = null,
    var gender: Gender,
    var email: String,
    var password: String,
    var phone: Int,
) {
    fun toEntity(): User {
        return User(gender = gender, email = email, password = password, phone = phone)
    }

    fun toUpdateEntity(): User {
        return User(id = id, gender = gender, email = email, password = password, phone = phone)
    }

}