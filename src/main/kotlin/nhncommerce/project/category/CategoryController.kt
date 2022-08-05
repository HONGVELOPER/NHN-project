package nhncommerce.project.category

import nhncommerce.project.page.PageRequestDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("categories")
class CategoryController(private val categoryService: CategoryService) {
    //사용 안함

    //api 명세서에 없는것
    //category 리스트
    @GetMapping("/get")
    fun getCategories(model : Model) : String {

        val categories = categoryService.getCategories()

        model.addAttribute("categoryList", categories)
        return "category/category"
    }

    //카테고리 조회
    @GetMapping("{categoryId}")
    fun getProductsByCategory(@PathVariable("categoryId") categoryId : Long, model: Model):String {
        val parentCategoryDTO = categoryService.findParentCategory(categoryId)
        val childCategoryList = categoryService.findChildCategory(parentCategoryDTO)
        val products = categoryService.findProductList(categoryId, PageRequestDTO())

        model.addAttribute("categoryId", categoryId)
        model.addAttribute("products", products) // 2페이징 조회
        model.addAttribute("parentCategoryDTO", parentCategoryDTO)
        model.addAttribute("childCategoryList", childCategoryList)
        return "category/category"
    }







}