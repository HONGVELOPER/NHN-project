package nhncommerce.project.exception

import nhncommerce.project.util.alert.alertDTO
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

@ControllerAdvice
class ExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)
//    @ExceptionHandler(Exception::class)
//    fun except(e : Exception) : String{
//        return "errorPage/errorPage"
//    }

    @ExceptionHandler(AlertException::class)
    fun handleAlertException(ex: AlertException): ModelAndView {
        val mav: ModelAndView = ModelAndView()
        val error = ex.errorMessage
        mav.addObject("data", alertDTO(error.message, error.href))
        mav.viewName = "user/alert"
        logger.info("ERROR MESSAGE: ${error.message}")
        return mav
    }

    @ExceptionHandler(RedirectException::class)
    fun handleRedirectException(ex: RedirectException): ModelAndView {
        val mav: ModelAndView = ModelAndView()
        mav.addObject("data", ex.alertDTO)
        mav.viewName = "user/alert"
        return mav
    }
}