package nhncommerce.project.option

import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.option.domain.OptionStockDTO
import nhncommerce.project.product.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/options")
class OptionController (private val optionService: OptionService, private val productService: ProductService) {

    //필요 없음
//    @GetMapping("")
//    fun getOptions(model : Model) : String {
//        val options = optionService.getOptions()
//
//        model.addAttribute("optionList", options)
//        return "option/option"
//    }

    // 필요없음
//    @GetMapping("{optionId}")
//    fun updateOptionForm(@PathVariable(name = "optionId") optionId : Long, model : Model) : String {
//        val option = optionService.getOption(optionId)
//        if (option == null)
//            return "redirect:/options"
//        model.addAttribute("option", option)
//        return "optionDetail"
//    }

//    @PutMapping("")
//    fun updateOption(@ModelAttribute optionDTO: OptionDTO , model : Model) : String{
//        println("-----")
//        optionService.updateOption(optionDTO.optionId?:0, optionDTO)
//        return "redirect:/options"
//    }

    //필요없음
//    @DeleteMapping("{optionId}")
//    fun deleteOption(@PathVariable(name = "optionId") optionId : Long) : String {
//        optionService.deleteOption(optionId)
//
//        return "redirect:/options"
//    }

    //삭제 예정
    //product 등록시 같이 들어감
    //product 등록 및 옵션 등록
//    @GetMapping("new")
//    fun createOptionPage(model : Model) : String {
//
//        var optionListDTO = OptionListDTO()
//
//        model.addAttribute("optionList", optionListDTO)
//
//        return "option/createOption"
//    }

    //삭제 예정
    //product 등록시 같이 들어감
    //product 등록 및 옵션 등록
//    @PostMapping("")
//    fun createOption(@ModelAttribute optionList : OptionListDTO) : String {
//
//        optionService.createOptionDetail(optionList)
//
//        println(optionList.toString())
//        return "redirect:/options"
//    }

    //삭제 예정
    //product 리스트
//    @GetMapping("products")
//    fun getProductList(model : Model) : String{
//        //임시로 optionService에서 product를 가져옴
//        val productList = optionService.getProductList()
//
//        model.addAttribute("productList", productList)
//        return "option/productList"
//    }

    /**
    //옵션 수정 페이지
    @GetMapping("products/{productId}/type")
    fun updateProductOptionPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val productOptionList = optionService.getProductOptionList(productId)

        model.addAttribute("optionList", productOptionList)
        return "option/updateOptionList"
    }





//    이런 방식으로 옵션을 수정하는것은 문제가 있다.
    //옵션 수정에서 삭제
    @GetMapping("{optionId}/products/{productId}")
    fun deleteProductOption(@PathVariable("optionId") optionId : Long, @PathVariable("productId") productId: Long ,redirect : RedirectAttributes) : String {

        optionService.deleteOption(optionId)

        redirect.addAttribute("productId", productId )
        return "redirect:/options/products/{productId}/type"
    }
    **/

    //옵션 수정
    @GetMapping("products/{productId}/type")
    fun updateProductOptionPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val productOptionList = optionService.getProductOptionList(productId)

        model.addAttribute("optionList", productOptionList)
        return "option/updateOptionList"
    }

    //옵션 초기화
    @DeleteMapping("products/{productId}")
    fun deleteProductOption(@PathVariable(name = "productId") productId: Long, model: Model) : String{
        optionService.deleteOptions(productId)

        var optionListDTO = OptionListDTO()
        optionListDTO.productDTO = productService.getProduct(productId).toProductDTO()

        model.addAttribute("optionList", optionListDTO)
        return "option/recreateOption"
    }

    @PostMapping("products/{productId}")
    fun updateProductOption(@PathVariable(name = "productId") productId: Long, redirect : RedirectAttributes, @ModelAttribute optionList : OptionListDTO) : String{
        val productDTO = productService.getProductDTO(productId)
        optionList.productDTO = productDTO
        optionService.createOptionDetail(optionList)
        redirect.addAttribute("productId", productId )
        return "redirect:/options/products/{productId}/type"

    }

    //product 재고수정 (이름 수정 필요)
    @GetMapping("products/{productId}/stock")
    fun updateOptionDetailPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val optionDetails = optionService.getProductOptionDetails(productId)
        model.addAttribute("optionDetailList", optionDetails)

        return "option/optionDetailList"
    }

    //product 재고수정 (이름 수정 필요)
    @PutMapping("products")
    fun updateOptionDetail(@ModelAttribute optionStockDTO: OptionStockDTO ,model : Model) : String {
        optionService.updateOptionDetail(optionStockDTO)

        return "redirect:/products"
    }


}