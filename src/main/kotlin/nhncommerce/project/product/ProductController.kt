package nhncommerce.project.product


import nhncommerce.project.category.CategoryService
import nhncommerce.project.option.OptionService
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import nhncommerce.project.review.ReviewService
import nhncommerce.project.review.domain.Review
import nhncommerce.project.review.domain.ReviewListDTO
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@Controller
class ProductController(
    val productService: ProductService,
    val optionService: OptionService,
    val categoryService: CategoryService,
    val storageTokenService: StorageTokenService,
    val reviewService: ReviewService
) {

    @GetMapping("/")
    fun userHome(): String {
        return "redirect:/products"
    }

    @GetMapping("/products")
    fun getProductList(model : Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/userProductList"
    }

    @GetMapping("/admin/addProductPage")
    fun addProductPage(model : Model):String{
        val productOptionDTO = ProductOptionDTO()
        val categoryListDTO = categoryService.getCategoryList()
        storageTokenService.accessToken()
        model.addAttribute("categoryListDTO", categoryListDTO)
        model.addAttribute("productOptionDTO", productOptionDTO)
        return "product/addProduct"
    }

    @GetMapping("/admin/products")
    fun productListPage(model : Model, pageRequestDTO: PageRequestDTO):String{
        model.addAttribute("products",productService.getProductList(pageRequestDTO))
        return "product/productList"
    }

    @GetMapping("/admin/updateProductPage/{productId}")
    fun updateProductPage(@PathVariable("productId")productId :String, productDTO: ProductDTO,model: Model) : String{
        val productDTO = productService.getProduct(productId)
        model.addAttribute("productImageDTOList", productService.getProductImageDTOList(productDTO.dtoToEntity()))
        model.addAttribute("categoryListDTO", categoryService.getCategoryList())
        model.addAttribute("productDTO", productDTO)
        model.addAttribute("thumbnail", productService.getThumbnail(productId))
        return "product/updateProduct"
    }

    @PostMapping("/admin/products")
    fun createProduct(@Valid productOptionDTO: ProductOptionDTO,bindingResult: BindingResult,
                      response: HttpServletResponse, session : HttpSession, model: Model,
                        @RequestPart file : MultipartFile,  @RequestPart(value="fileList", required=false) fileList : List<MultipartFile>) : String{
        if(bindingResult.hasErrors()){
            model.addAttribute("categoryListDTO", categoryService.getCategoryList())
            return "product/addProduct"
        }
        val separate = productService.separate(productOptionDTO)
        val createProduct = productService.createProduct(separate.get(0) as ProductDTO,file.inputStream)
        productService.createProductImageList(fileList , createProduct) //이미지 저장
        val optionListDTO = separate.get(1) as OptionListDTO
        optionListDTO.productDTO = createProduct.entityToDto()
        optionService.createOptionDetail(optionListDTO)
        return "redirect:/admin/products"
    }

    @PutMapping("/admin/products/{productId}")
    fun updateProduct(@Valid productDTO: ProductDTO,bindingResult: BindingResult,
                      categoryId : String, model: Model,@PathVariable("productId")productId : String,
                      @RequestPart file : MultipartFile, @RequestPart(value="fileList", required=false) fileList : List<MultipartFile>) : String{
        if(bindingResult.hasErrors()){
            model.addAttribute("categoryListDTO", categoryService.getCategoryList())
            return "product/updateProduct"
        }
        productService.createProductImageList(fileList , productDTO.dtoToEntity()) //이미지 저장
        productDTO.category = categoryService.getCategoryById(categoryId.toLong())
        productService.updateProduct(productDTO,file)
        return "redirect:/admin/products"
    }

    @DeleteMapping("/admin/products/{productId}")
    fun deleteProduct(@PathVariable("productId")productId : String) : String{
        productService.deleteProduct(productId)
        return "redirect:/admin/products"
    }
    @GetMapping("/products/{productId}")
    fun getProductDetail(
        @PathVariable("productId") productId : Long,
        pageRequestDTO: PageRequestDTO,
        model : Model
    ) : String {
        val productDTO = productService.getProductDTO(productId)
        val optionDetailDTOList = optionService.getProductOptionDetails(productId)
        val imageDTOList = productService.getProductImageDTOList(productDTO.dtoToEntity())
        val result: PageResultDTO<ReviewListDTO, Review> =
            reviewService.findReviewListByProduct(productId, pageRequestDTO)
        model.addAttribute("reviews", result)
        model.addAttribute("productImageDTOList", imageDTOList)
        model.addAttribute("optionDetailList", optionDetailDTOList)
        model.addAttribute("productDTO", productDTO)
        return "product/productDetail"
    }

    @DeleteMapping("/admin/products/{productId}/{imageId}")
    fun deleteProductImage(@PathVariable("imageId") imageId : Long, @PathVariable("productId") productId : Long ,redirect : RedirectAttributes) : String {
        productService.deleteProductImage(imageId)

        redirect.addAttribute("productId", productId )
        return "redirect:/admin/updateProductPage/{productId}"
    }

}