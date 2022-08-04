package nhncommerce.project.category

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("categories")
class CategoryController(private val categoryService: CategoryService) {
    //api 명세서에 없는것
    //category 리스트
    @GetMapping("/get")
    fun getCategories(model : Model) : String {

        val categories = categoryService.getCategories()

        model.addAttribute("categoryList", categories)
        return "category/category"
    }

    //대 카테고리
    @GetMapping("{categoryId}")
    fun getProductsByCategory(@PathVariable("categoryId") categoryId : Long, model: Model):String {
        val parentCategoryDTO = categoryService.findParentCategory(categoryId)
        val childCategoryList = categoryService.findChildCategory(parentCategoryDTO) //무조건 DTO로 받아야하나?
        val productDTOList = categoryService.findProducts(categoryId)

        model.addAttribute("productDTOList", productDTOList)
        model.addAttribute("parentCategoryDTO", parentCategoryDTO)
        model.addAttribute("childCategoryList", childCategoryList)
        return "category/category"
    }




}