package nhncommerce.project

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun except(e : Exception) : String{
        return "errorPage/errorPage"
    }
}