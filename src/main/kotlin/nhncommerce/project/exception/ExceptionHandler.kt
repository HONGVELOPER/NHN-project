package nhncommerce.project.exception

import nhncommerce.project.util.alert.AlertService
import nhncommerce.project.util.alert.alertDTO
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse


@ControllerAdvice
class ExceptionHandler(
    val alertService: AlertService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun MaxUploadSizeExceededExceptionHandler(e: MaxUploadSizeExceededException, response : HttpServletResponse){
        alertService.alertMessage("사진은 10MB 이상 업로드 할 수 없습니다.","/admin/addProductPage",response)
    }

    @ExceptionHandler(AlertException::class)
    fun handleAlertException(ex: AlertException): ModelAndView {
        val mav: ModelAndView = ModelAndView()
        val error = ex.errorMessage
        mav.addObject("data", alertDTO(error.message, error.href))
        mav.viewName = "user/alert"
        logger.info("ERROR MESSAGE: ${error.message}")
        return mav
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(): ModelAndView {
        val mav: ModelAndView = ModelAndView()
        mav.addObject("data", alertDTO("해당 데이터를 찾을 수 없습니다.", "/products"))
        mav.viewName = "user/alert"
        logger.info("ERROR MESSAGE: ${error.message}")
        return mav
    }

}