package nhncommerce.project.product

import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.product.domain.ProductDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class ProductController(
    val productService : ProductService
) {

    @GetMapping("/addProductPage")
    fun addProductPage(productDTO: ProductDTO):String{
        return "product/addProduct"
    }

    /**
     * 상품 전제 조회
     */
    @GetMapping("/products")
    fun productListPage(model : Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/productList"
    }

    /**
     * 상품 등록
     */
    //todo 세션 말고 쿠키로 할것 나중에 수정하기
    @PostMapping("/products")
    fun createProduct(@Valid productDTO: ProductDTO,bindingResult: BindingResult ,response: HttpServletResponse, session : HttpSession):String{
        productService.createProduct(productDTO)
        if(bindingResult.hasErrors()){
            session.setAttribute("productName",productDTO.productName)
            session.setAttribute("price",productDTO.price)
            session.setAttribute("briefDescription",productDTO.briefDescription)
            session.setAttribute("detailDescription",productDTO.detailDescription)
            return "product/addProduct"
        }
        println(productDTO.toString())
        return "redirect:/products"
    }

}