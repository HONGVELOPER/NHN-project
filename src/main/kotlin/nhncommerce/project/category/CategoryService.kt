package nhncommerce.project.category

import nhncommerce.project.category.domain.Category
import nhncommerce.project.category.domain.CategoryDTO
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.product.domain.ProductDTO
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CategoryService (
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    //카테고리 조회
    fun getCategories() : List<CategoryDTO>{
        val categories = categoryRepository.findAll()
        return categories.map{ it.toCategoryDTO() }
    }

    //카테고리 생성
    fun createCategory(postCategoryDTO: CategoryDTO) : CategoryDTO {
        val category = categoryRepository.save(postCategoryDTO.toEntity())
        return category.toCategoryDTO()
    }
    //부모 카테고리 찾기
    fun findParentCategory(categoryId : Long) : CategoryDTO? {
        val findCategory = categoryRepository.findById(categoryId).get()
        if (findCategory.parentCategory == null)
            return findCategory.toCategoryDTO()
        else
            return findCategory.parentCategory?.toCategoryDTO()
    }

    fun findChildCategory(parentCategoryDTO: CategoryDTO?) : List<Category> {
        return categoryRepository.findCategoriesByParentCategory(parentCategoryDTO?.toEntity())
    }

    fun findProducts(categoryId : Long) : List<ProductDTO> {
        return productRepository.findProductsByCategoryId(categoryId).map { it.toProductDTO()}
    }

}