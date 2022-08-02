package nhncommerce.project.option

import lombok.Getter
import nhncommerce.project.option.domain.Option
import nhncommerce.project.option.domain.OptionDTO
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.option.domain.OptionStockDTO
import nhncommerce.project.product.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/options")
class OptionController (private val optionService: OptionService) {

    @GetMapping("")
    fun getOptions(model : Model) : String {
        val options = optionService.getOptions()

        model.addAttribute("optionList", options)
        return "option/option"
    }

    @GetMapping("{optionId}")
    fun updateOptionForm(@PathVariable(name = "optionId") optionId : Long, model : Model) : String {
        val option = optionService.getOption(optionId)
        if (option == null)
            return "redirect:/options"
        model.addAttribute("option", option)
        return "optionDetail"
    }

//    @PutMapping("")
//    fun updateOption(@ModelAttribute optionDTO: OptionDTO , model : Model) : String{
//        println("-----")
//        optionService.updateOption(optionDTO.optionId?:0, optionDTO)
//        return "redirect:/options"
//    }

    @DeleteMapping("{optionId}")
    fun deleteOption(@PathVariable(name = "optionId") optionId : Long) : String {
        optionService.deleteOption(optionId)

        return "redirect:/options"
    }

    //product 등록시 같이 들어감
    //product 등록 및 옵션 등록
    @GetMapping("new")
    fun createOptionPage(model : Model) : String {

        var optionListDTO = OptionListDTO()

        model.addAttribute("optionList", optionListDTO)

        return "option/createOption"
    }

    //product 등록시 같이 들어감
    //product 등록 및 옵션 등록
    @PostMapping("")
    fun createOption(@ModelAttribute optionList : OptionListDTO) : String {

        optionService.createOptionDetail(optionList)

        println(optionList.toString())
        return "redirect:/options"
    }


    //옵션 수정
    @GetMapping("products/{productId}/edit")
    fun updateProductOption(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val productOptionList = optionService.getProductOptionList(productId)

        model.addAttribute("optionList", productOptionList)
        return "option/updateOptionList"
    }

    //product 리스트
    @GetMapping("products")
    fun getProductList(model : Model) : String{
        //임시로 optionService에서 product를 가져옴
        val productList = optionService.getProductList()

        model.addAttribute("productList", productList)
        return "option/productList"
    }

    //product 재고수정 (이름 수정 필요)
    @GetMapping("products/{productId}")
    fun updateOptionDetailPage(@PathVariable(name = "productId") productId : Long, model : Model) : String {
        val optionDetails = optionService.getProductOptionDetails(productId)
        model.addAttribute("optionDetailList", optionDetails)

        return "option/optionDetailList"
    }

    //product 재고수정 (이름 수정 필요)
    @PutMapping("products")
    fun updateOptionDetail(@ModelAttribute optionStockDTO: OptionStockDTO ,model : Model) : String {
        optionService.updateOptionDetail(optionStockDTO)

        return "redirect:/options"
    }


}