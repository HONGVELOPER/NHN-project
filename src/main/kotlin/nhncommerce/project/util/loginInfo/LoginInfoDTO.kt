package nhncommerce.project.util.loginInfo

data class LoginInfoDTO(
    val isLogin: Boolean = false,
    val isAdmin: Boolean = false,
    val userId: Long = 0L,
) {
}