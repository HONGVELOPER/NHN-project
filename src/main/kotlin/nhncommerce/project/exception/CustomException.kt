package nhncommerce.project.exception

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException() {}