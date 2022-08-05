package nhncommerce.project.product


import nhncommerce.project.category.CategoryService
import nhncommerce.project.baseentity.Status
import nhncommerce.project.image.imageService
import nhncommerce.project.option.OptionService
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.stream.IntStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class ProductController(
    val productService : ProductService,
    val optionService: OptionService,
    val categoryService: CategoryService,
    val imageService: imageService

) {

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/addProductPage")
    fun addProductPage(model : Model):String{
        val productOptionDTO = ProductOptionDTO()
        val categoryListDTO = categoryService.getCategoryList()

        model.addAttribute("categoryListDTO", categoryListDTO)
        model.addAttribute("productOptionDTO", productOptionDTO)
        return "product/addProduct"
    }

    /**
     * 상품 전제 조회 페이지
     */
    @GetMapping("/products")
    fun productListPage(model : Model, pageRequestDTO: PageRequestDTO):String{

        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/productList"
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/updateProductPage/{productId}")
    fun updateProduct(@PathVariable("productId")productId :String, productDTO: ProductDTO,model: Model) : String{
        model.addAttribute("productDTO",productService.getProduct(productId))
        return "product/updateProduct"
    }

    /**
     * 상품 등록
     */
    //todo 세션 말고 쿠키로 할것 나중에 수정하기
    @PostMapping("/products")
    fun createProduct(@Valid productOptionDTO: ProductOptionDTO,bindingResult: BindingResult,
                      response: HttpServletResponse, session : HttpSession,
                        @RequestPart file : MultipartFile) : String{
        if(bindingResult.hasErrors()){
            session.setAttribute("productName",productOptionDTO.productName)
            session.setAttribute("price",productOptionDTO.price)
            session.setAttribute("briefDescription",productOptionDTO.briefDescription)
            session.setAttribute("detailDescription",productOptionDTO.detailDescription)
            return "product/addProduct"
        }
        println("=============")
        println(productOptionDTO.categoryId)
        val separate = productService.separate(productOptionDTO)
        val createProduct = productService.createProduct(separate.get(0) as ProductDTO,file.inputStream)
        val optionListDTO = separate.get(1) as OptionListDTO
        optionListDTO.product = createProduct
        optionService.createOptionDetail(optionListDTO)
        return "redirect:/products"
    }

    /**
     * 상품 수정
     */
    @PutMapping("/admin/products/{productId}")
    fun updateProduct(@PathVariable("productId")productId : String,productDTO: ProductDTO,
                      @RequestPart file : MultipartFile) : String{
        productService.updateProduct(productDTO,file.inputStream)
        return "redirect:/products"
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/admin/products/{productId}")
    fun deleteProduct(@PathVariable("productId")productId : String) : String{
        productService.deleteProduct(productId)
        return "redirect:/products"
    }

}