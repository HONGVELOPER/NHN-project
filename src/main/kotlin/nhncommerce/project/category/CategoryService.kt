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
@Transactional
//todo 트랜잭션 필요할때만 사용한다.
class CategoryService (
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) {

    //카테고리 생성
    fun createCategory(postCategoryDTO: CategoryDTO) : CategoryDTO {
        val category = categoryRepository.save(postCategoryDTO.dtoToEntity())
        return category.entityToDto()
    }

    fun getCategoryById(categoryId : Long) : Category{
        return categoryRepository.findById(categoryId).get()
    }

    //카테고리 조회
    fun getCategories() : List<CategoryDTO>{
        val categories = categoryRepository.findAll()
        return categories.map{ it.entityToDto() }
    }

    //부모 카테고리 찾기
    //todo : if 문은 리턴한다.
    fun findParentCategory(categoryId : Long) : CategoryDTO? {
        val findCategory = categoryRepository.findById(categoryId).get()
        if (findCategory.parentCategory == null)
            return findCategory.entityToDto()
        else
            return findCategory.parentCategory?.entityToDto()
    }

    //자식 카테고리 찾기
    fun findChildCategory(parentCategoryDTO: CategoryDTO?) : List<Category> {
        return categoryRepository.findCategoriesByParentCategory(parentCategoryDTO?.dtoToEntity())
    }

    //product 생성 및 수정을 위한 category List
    //todo : todo 문자열 과 map 수정
    fun getCategoryList():List<CategoryListDTO> {
        val list = mutableListOf<CategoryListDTO>()
        val categories = categoryRepository.findAllByParentCategoryIsNotNull()
        categories.map {
            val categoryListDTO = CategoryListDTO(it.categoryId, it.parentCategory?.name + " > " + it.name)
            list.add(categoryListDTO)
        }
        return list.toList()
    }

    //해당 카테고리의 productList 조회
    //todo : 삭제
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

    //todo : 노란줄 수정하자
    //카테고리 조회 및 페이징 처리
    fun findProductList(categoryId : Long, pageRequestDTO: PageRequestDTO) : PageResultDTO<ProductDTO, Product>{
        val category = categoryRepository.findById(categoryId).get()
        pageRequestDTO.size = 12 //todo : 메서드로 수정하자 왜 12인지 적는것도 중요
        val sort = categorySort(pageRequestDTO)
        val pageable = pageRequestDTO.getPageable(sort)
        var booleanBuilder = BooleanBuilder()
        if (category.parentCategory == null){
            val childCategoryList = categoryRepository.findCategoriesByParentCategory(category).map { it.categoryId!! }
            booleanBuilder = getParentCategorySearch(pageRequestDTO, childCategoryList)
        } else {
            booleanBuilder = getChildCategorySearch(pageRequestDTO, category)
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
       // val type = pageRequestDTO.type
        val booleanBuilder = BooleanBuilder()
        val qProduct = QProduct.product
      //  val keyword = pageRequestDTO.keyword
        //자식 categoryId에 해당하는 product 검색
        for (categoryId in categoryIdList){
            booleanBuilder.or(qProduct.category.categoryId.eq(categoryId))
        }

        return booleanBuilder
    }

    //소 카테고리 조회 (패이징)
    fun getChildCategorySearch(pageRequestDTO: PageRequestDTO, category: Category) : BooleanBuilder {
       // val type = pageRequestDTO.type
        val booleanBuilder = BooleanBuilder()
        val qProduct = QProduct.product
       // val keyword = pageRequestDTO.keyword
        val expression = qProduct.category.eq(category).and(qProduct.status.eq(Status.ACTIVE))

        booleanBuilder.and(expression)

        return booleanBuilder
    }

}