package nhncommerce.project.product

import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class ProductController(
    val productService : ProductService
) {

    @GetMapping("/")
    fun addProductPage(productDTO: ProductDTO):String{
        return "product/addProduct"
    }

    /**
     * 상품 전제 조회
     */
    @GetMapping("/products")
    fun getProducts(model : Model):String{
        val products = productService.getProducts()
        model.addAttribute("products",products)
        return ""
    }



    @GetMapping("/index")
    fun indexPage():String{
        return "test"
    }

    @PostMapping("/products")
    fun createProduct(@Valid productDTO: ProductDTO,bindingResult: BindingResult ,response: HttpServletResponse, session : HttpSession):String{
        if(bindingResult.hasErrors()){
            session.setAttribute("productName",productDTO.productName)
            session.setAttribute("price",productDTO.price)
            session.setAttribute("briefDescription",productDTO.briefDescription)
            session.setAttribute("detailDescription",productDTO.detailDescription)
            return "product/addProduct"
        }
        println(productDTO.toString())
        return "redirect:/index"
    }

}