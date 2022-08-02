package nhncommerce.project.category

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/category")
class CategoryController(private val categoryService: CategoryService) {
    //api 명세서에 없는것
    //category 리스트
    @GetMapping("/get")
    fun getCategories(model : Model) : String {

        val categories = categoryService.getCategories()

        model.addAttribute("categoryList", categories)
        return "category/category"
    }
}