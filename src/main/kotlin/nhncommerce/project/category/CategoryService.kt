package nhncommerce.project.category

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import nhncommerce.project.category.domain.CategoryDTO
import nhncommerce.project.category.domain.CategoryListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.ProductRepository
import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.QProduct
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.function.Function
import javax.transaction.Transactional

@Service
class CategoryService (
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    fun getCategoryById(categoryId : Long) : Category{
        return categoryRepository.findById(categoryId).get()
    }

    //부모 카테고리 찾기
    fun findParentCategory(categoryId : Long) : CategoryDTO? {
        val findCategory = categoryRepository.findById(categoryId).get()
        return if (findCategory.parentCategory == null) findCategory.entityToDto()
                else findCategory.parentCategory.entityToDto()
    }

    //자식 카테고리 찾기
    fun findChildCategory(parentCategoryDTO: CategoryDTO?) : List<Category> {
        return categoryRepository.findCategoriesByParentCategory(parentCategoryDTO?.dtoToEntity())
    }

    //product 생성 및 수정을 위한 category List
    fun getCategoryList() : List<CategoryListDTO> {
        val categories = categoryRepository.findAllByParentCategoryIsNotNull()
        return categories.map {
            CategoryListDTO(it.categoryId, "${it.parentCategory?.name} > ${it.name}")
        }
    }

    //카테고리 조회 및 페이징 처리
    fun findProductList(categoryId : Long, pageRequestDTO: PageRequestDTO) : PageResultDTO<ProductDTO, Product>{
        val category = categoryRepository.findById(categoryId).get()
        pageRequestDTO.changeSize(12) // 상품 리스트 격자 3 * 4
        val sort = categorySort(pageRequestDTO)
        val pageable = pageRequestDTO.getPageable(sort)
        val booleanBuilder = if (category.parentCategory == null){
            val childCategoryList = categoryRepository.findCategoriesByParentCategory(category).map { it.categoryId }
            getParentCategorySearch(pageRequestDTO, childCategoryList)
        } else {
            getChildCategorySearch(pageRequestDTO, category)
        }
        val result = productRepository.findAll(booleanBuilder, pageable)
        val fn : Function<Product, ProductDTO> =
            Function<Product, ProductDTO> { entity: Product? -> entity?.entityToDto() }
        return PageResultDTO<ProductDTO, Product>(result, fn)
    }

    fun categorySort(pageRequestDTO: PageRequestDTO) : Sort {
        var sort : Sort = Sort.by("updatedAt").descending()

        val type = pageRequestDTO.type

        if(type.contains("price")){
            sort = Sort.by("price").descending()
        }
        if(type.contains("star")){
            sort = Sort.by("totalStar").descending()
        }

        return sort
    }

    //대 카테고리 조회 (페이징)
    fun getParentCategorySearch(pageRequestDTO: PageRequestDTO, categoryIdList : List<Long>) : BooleanBuilder {
        val booleanBuilder = BooleanBuilder()
        val qProduct = QProduct.product

        for (categoryId in categoryIdList)
            booleanBuilder.or(qProduct.category.categoryId.eq(categoryId))

        return booleanBuilder
    }

    //소 카테고리 조회 (패이징)
    fun getChildCategorySearch(pageRequestDTO: PageRequestDTO, category: Category) : BooleanBuilder {
        val booleanBuilder = BooleanBuilder()
        val qProduct = QProduct.product
        val expression = qProduct.category.eq(category).and(qProduct.status.eq(Status.ACTIVE))

        booleanBuilder.and(expression)

        return booleanBuilder
    }

}