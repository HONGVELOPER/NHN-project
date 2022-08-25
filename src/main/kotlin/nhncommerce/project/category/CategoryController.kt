package nhncommerce.project.category

import nhncommerce.project.page.PageRequestDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping("{categoryId}")
    fun getProductsByCategory(
        @PathVariable("categoryId") categoryId : Long,
        model: Model,
        pageRequestDTO: PageRequestDTO
    ):String {
        val parentCategoryDTO = categoryService.findParentCategory(categoryId)
        val childCategoryList = categoryService.findChildCategory(parentCategoryDTO)
        val products = categoryService.findProductList(categoryId, pageRequestDTO)

        model.addAttribute("categoryId", categoryId)
        model.addAttribute("products", products)
        model.addAttribute("parentCategoryDTO", parentCategoryDTO)
        model.addAttribute("childCategoryList", childCategoryList)
        model.addAttribute("type", pageRequestDTO.type)
        return "category/category"
    }
}