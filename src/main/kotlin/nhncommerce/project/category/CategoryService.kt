package nhncommerce.project.category

import nhncommerce.project.category.domain.CategoryDTO
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CategoryService ( private val categoryRepository: CategoryRepository) {
    //카테고리 조회
    fun getCategories() : List<CategoryDTO>{
        val categories = categoryRepository.findAll()
        return categories.map{ it.toCategoryDTO() }
    }

    //카테고리 생성
    @Transactional
    fun createCategory(postCategoryDTO: CategoryDTO) : CategoryDTO {
        val category = categoryRepository.save(postCategoryDTO.toEntity())
        return category.toCategoryDTO()
    }

}