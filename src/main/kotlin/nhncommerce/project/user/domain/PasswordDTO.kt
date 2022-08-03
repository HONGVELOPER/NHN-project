package nhncommerce.project.user.domain


data class PasswordDTO(
    var password: String = "",
    var newPassword: String = "",
    var newPasswordVerify: String = "",
) {

}