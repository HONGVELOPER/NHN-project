package nhncommerce.project.user.domain

import nhncommerce.project.baseentity.Status

data class UserDTO (
    var status: Status,
    var gender: Gender,
    var email: String,
    var password: String,
    var phone: Int,
) {
    fun toEntity(): User {
        return User(status = status, gender = gender, email = email, password = password, phone = phone)
    }
    
}