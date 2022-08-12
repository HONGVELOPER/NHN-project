package nhncommerce.project.exception

import nhncommerce.project.util.alert.alertDTO
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class ExceptionHandler {


//    @ExceptionHandler(Exception::class)
//    fun except(e : Exception) : String{
//        return "errorPage/errorPage"
//    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException) {
        println(ex.errorCode.message)
        println(ex.errorCode.status)
    }

    @ExceptionHandler(RedirectException::class)
    fun handleRedirectException(ex: RedirectException): ModelAndView {
        val mav: ModelAndView = ModelAndView()
        mav.addObject("data", ex.alertDTO)
        mav.viewName = "user/alert"
        return mav
    }
}