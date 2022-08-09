package nhncommerce.project.product


import nhncommerce.project.category.CategoryService
import nhncommerce.project.option.OptionService
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class ProductController(
    val productService: ProductService,
    val optionService: OptionService,
    val categoryService: CategoryService,

) {

    /**
     * 사용자 보유 쿠폰 리스트
     */
    @GetMapping("/products")
    fun getProductList(model : Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/userProductList"
    }

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/admin/addProductPage")
    fun addProductPage(model : Model):String{
        val productOptionDTO = ProductOptionDTO()
        val categoryListDTO = categoryService.getCategoryList()

        productService.generateToken()

        model.addAttribute("categoryListDTO", categoryListDTO)
        model.addAttribute("productOptionDTO", productOptionDTO)
        return "product/addProduct"
    }

    /**
     * 상품 전제 조회 페이지
     */
    @GetMapping("/admin/products")
    fun productListPage(model : Model, pageRequestDTO: PageRequestDTO):String{

        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/productList"
    }

    /**
     * 상품 수정 페이지
     */
    @GetMapping("/admin/updateProductPage/{productId}")
    fun updateProduct(@PathVariable("productId")productId :String, productDTO: ProductDTO,model: Model) : String{

        model.addAttribute("categoryListDTO", categoryService.getCategoryList())
        model.addAttribute("productDTO",productService.getProduct(productId))
        return "product/updateProduct"
    }

    /**
     * 상품 등록
     */
    @PostMapping("/admin/products")
    fun createProduct(@Valid productOptionDTO: ProductOptionDTO,bindingResult: BindingResult,
                      response: HttpServletResponse, session : HttpSession,
                        @RequestPart file : MultipartFile) : String{
        if(bindingResult.hasErrors()){
            session.setAttribute("productName",productOptionDTO.productName)
            session.setAttribute("price",productOptionDTO.price)
            session.setAttribute("briefDescription",productOptionDTO.briefDescription)
            session.setAttribute("detailDescription",productOptionDTO.detailDescription)
            // 카테고리 리스트를 위한 session
            session.setAttribute("categoryListDTO" , categoryService.getCategoryList())
            return "product/addProduct"
        }
        println(productOptionDTO.categoryId)
        val separate = productService.separate(productOptionDTO)
        val createProduct = productService.createProduct(separate.get(0) as ProductDTO,file.inputStream)
        val optionListDTO = separate.get(1) as OptionListDTO
        optionListDTO.productDTO = createProduct.toProductDTO()
        optionService.createOptionDetail(optionListDTO)
        return "redirect:/admin/products"
    }

    /**
     * 상품 수정
     */
    @PutMapping("/admin/products/{productId}")
    fun updateProduct(@Valid productDTO: ProductDTO,bindingResult: BindingResult,
                      categoryId : String, @PathVariable("productId")productId : String,
                      response: HttpServletResponse, session : HttpSession,
                      @RequestPart file : MultipartFile) : String{
        if(bindingResult.hasErrors()){
            session.setAttribute("thumbnail", productDTO.thumbnail)
            session.setAttribute("productName",productDTO.productName)
            session.setAttribute("price",productDTO.price)
            session.setAttribute("briefDescription",productDTO.briefDescription)
            session.setAttribute("detailDescription",productDTO.detailDescription)
            session.setAttribute("category", categoryService.getCategoryById(categoryId.toLong()))
            // 카테고리 리스트를 위한 session
            session.setAttribute("categoryList" , categoryService.getCategoryList())
            return "product/updateProduct"
        }
        productDTO.category = categoryService.getCategoryById(categoryId.toLong())
        productService.updateProduct(productDTO,file.inputStream)
        return "redirect:/admin/products"
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/admin/products/{productId}")
    fun deleteProduct(@PathVariable("productId")productId : String) : String{
        productService.deleteProduct(productId)
        return "redirect:/admin/products"
    }

    /**
     * 상품 상세
     */
    @GetMapping("products/{productId}")
    fun getProductDetail(@PathVariable("productId") productId : Long, model : Model ) : String {
        val productDTO = productService.getProductDTO(productId)
        val optionDetailDTOList = optionService.getProductOptionDetails(productId)

        model.addAttribute("optionDetailList", optionDetailDTOList)
        model.addAttribute("productDTO", productDTO)
        return "product/productDetail"
    }

}