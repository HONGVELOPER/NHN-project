package nhncommerce.project.category

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.domain.Category
import nhncommerce.project.category.domain.CategoryDTO
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
@Transactional
class CategoryService (
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {
    //카테고리 등록
    fun createCategory(category: Category){
        categoryRepository.save(category)
    }

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

    fun findProducts(categoryId : Long) : List<Product> {
        //카테고리
        val category = categoryRepository.findById(categoryId).get()
        if (category.parentCategory == null){
            val list = mutableListOf<Product>()
            //하위 카테고리 찾기
            val childCategoryList = categoryRepository.findCategoriesByParentCategory(category)
            //하위 카테고리의 product 찾기
            childCategoryList.map {
                productRepository.findProductsByCategoryId(it.categoryId!!).map {
                    list.add(it)
                }
            }
            return list.toList()
        } else {
            return productRepository.findProductsByCategoryId(categoryId)
        }
    }

    fun entityToDto(product: Product) : ProductDTO{
        val productDTO = ProductDTO(product.productId,product.status, product.productName, product.price, product.briefDescription,
            product.briefDescription, product.thumbnail, product.viewCount, product.totalStar)
        return productDTO
    }

    fun findProductList(categoryId : Long, pageRequestDTO: PageRequestDTO) : PageResultDTO<ProductDTO, Product>{
        val category = categoryRepository.findById(categoryId).get()
        pageRequestDTO.size = 12
        val pageable = pageRequestDTO.getPageable(Sort.by("productId").descending())
        var booleanBuilder = BooleanBuilder()
        if (category.parentCategory == null){
            val childCategoryList = categoryRepository.findCategoriesByParentCategory(category).map { it.categoryId!! }
            booleanBuilder = getParentCategorySearch(pageRequestDTO, childCategoryList)
        } else {
            booleanBuilder = getChildCategorySearch(pageRequestDTO, category)
        }
        val result = productRepository.findAll(booleanBuilder, pageable)

        val fn : Function<Product, ProductDTO> =
            Function<Product, ProductDTO> { entity: Product? -> entityToDto(entity!!) }
        return PageResultDTO<ProductDTO, Product>(result, fn)
    }

    fun getParentCategorySearch(pageRequestDTO: PageRequestDTO, categoryIdList : List<Long>) : BooleanBuilder {
        var type = pageRequestDTO.type
        var booleanBuilder = BooleanBuilder()
        var qProduct = QProduct.product
        var keyword = pageRequestDTO.keyword
        //자식 categoryId에 해당하는 product 검색
        for (categoryId in categoryIdList){
            booleanBuilder.or(qProduct.category.categoryId.eq(categoryId))
        }

        if(type == null || type.trim().isEmpty()){
            return booleanBuilder
        }
        //생략
        return booleanBuilder
    }

    fun getChildCategorySearch(pageRequestDTO: PageRequestDTO, category: Category) : BooleanBuilder {
        var type = pageRequestDTO.type
        var booleanBuilder = BooleanBuilder()
        var qProduct = QProduct.product
        var keyword = pageRequestDTO.keyword
        var expression = qProduct.category.eq(category).and(qProduct.status.eq(Status.ACTIVE))

        booleanBuilder.and(expression)

        if(type == null || type.trim().isEmpty()){
            return booleanBuilder
        }
        //생략
        return booleanBuilder
    }

}