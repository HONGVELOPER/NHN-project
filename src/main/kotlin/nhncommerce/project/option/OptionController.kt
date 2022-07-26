package nhncommerce.project.option

import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.option.domain.OptionStockDTO
import nhncommerce.project.product.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/options")
class OptionController (
    private val optionService: OptionService,
    private val productService: ProductService)
{
    @GetMapping("/products/{productId}/type")
    fun updateProductOptionPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val productOptionList = optionService.getProductOptionList(productId)

        model.addAttribute("optionList", productOptionList)
        return "option/updateOptionList"
    }

    @DeleteMapping("/products/{productId}")
    fun deleteProductOption(@PathVariable(name = "productId") productId: Long, model: Model) : String{
        optionService.deleteOptions(productId)

        val optionListDTO = OptionListDTO().apply {
            productDTO = productService.getProduct(productId).entityToDto()
        }
        //optionListDTO.productDTO = productService.getProduct(productId).entityToDto()

        model.addAttribute("optionList", optionListDTO)
        return "option/recreateOption"
    }

    @PostMapping("/products/{productId}")
    fun updateProductOption(@PathVariable(name = "productId") productId: Long, redirect : RedirectAttributes, @ModelAttribute optionList : OptionListDTO) : String{
        val productDTO = productService.getProductDTO(productId)
        optionList.productDTO = productDTO
        optionService.createOptionDetail(optionList)
        redirect.addAttribute("productId", productId )
        return "redirect:/admin/options/products/{productId}/type"
    }

    @GetMapping("/products/{productId}/stock")
    fun updateOptionDetailPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val optionDetails = optionService.getProductOptionDetails(productId)
        model.addAttribute("optionDetailList", optionDetails)
        return "option/optionDetailList"
    }

    @PutMapping("/products")
    fun updateOptionDetail(@ModelAttribute optionStockDTO: OptionStockDTO ,model : Model) : String {
        optionService.updateOptionDetail(optionStockDTO)
        return "redirect:/admin/products"
    }
}