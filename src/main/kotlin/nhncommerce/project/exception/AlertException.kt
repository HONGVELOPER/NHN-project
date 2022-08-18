package nhncommerce.project.exception

class AlertException(
    val errorMessage: ErrorMessage
) : RuntimeException() {}