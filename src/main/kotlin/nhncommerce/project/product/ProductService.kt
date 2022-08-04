package nhncommerce.project.product

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.option.OptionRepository
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import nhncommerce.project.product.domain.QProduct
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.function.Function

@Service
class ProductService(
    val productRepository: ProductRepository,
) {

    fun dtoTOEntity(productDTO: ProductDTO) : Product{
        val product = Product(status = Status.ACTIVE, productName = productDTO.productName, price = productDTO.price,
                briefDescription = productDTO.briefDescription, detailDescription = productDTO.detailDescription,
                thumbnail = productDTO.thumbnail, viewCount = productDTO.viewCount, totalStar = productDTO.totalStar)
        return product
    }

    fun entityToDto(product: Product) : ProductDTO{
        val productDTO = ProductDTO(product.productId,product.status, product.productName, product.price, product.briefDescription,
                                    product.briefDescription, product.thumbnail, product.viewCount, product.totalStar)
        return productDTO
    }

    fun separate(productOptionDTO: ProductOptionDTO) : MutableList<Any>{
        val productDTO = ProductDTO(
            null,
            Status.ACTIVE,
            productOptionDTO.productName,
            productOptionDTO.price,
            productOptionDTO.briefDescription,
            productOptionDTO.detailDescription,
            productOptionDTO.thumbnail,
            productOptionDTO.viewCount,
        )
        val optionListDTO = OptionListDTO(
            null,
           productOptionDTO.option1,
           productOptionDTO.option2,
           productOptionDTO.option3,
           productOptionDTO.option1List,
           productOptionDTO.option2List,
           productOptionDTO.option3List
       )

        val objectList = mutableListOf<Any>()
        objectList.add(productDTO)
        objectList.add(optionListDTO)
        return objectList
    }

    fun createProduct(productDTO: ProductDTO) : Product{
        val product = dtoTOEntity(productDTO)
        return productRepository.save(product)
    }

    /**
     * 상품 전제 조회
     */
    fun getProductList(pageRequestDTO: PageRequestDTO) : PageResultDTO<ProductDTO,Product>{
        pageRequestDTO.size=12
        val pageable = pageRequestDTO.getPageable(Sort.by("productId").descending())
        var booleanBuilder = getSearch(pageRequestDTO)
        val result = productRepository.findAll(booleanBuilder,pageable)

        val fn: Function<Product, ProductDTO> =
                Function<Product, ProductDTO> { entity: Product? -> entityToDto(entity!!) }

        return PageResultDTO<ProductDTO,Product>(result,fn)
    }

    fun getProduct(productId: Long) : Product {
        return productRepository.findById(productId).get()
    }

    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

        var type = pageRequestDTO.type

        var booleanBuilder = BooleanBuilder()

        var qProduct = QProduct.product

        var keyword = pageRequestDTO.keyword

        var expression = qProduct.productId.gt(0L)

        booleanBuilder.and(expression)

        if(type == null || type.trim().isEmpty()){
            return booleanBuilder
        }

        var conditionBuilder = BooleanBuilder()

        if(type.contains("productName")){
            conditionBuilder.or(qProduct.productName.contains(keyword))
        }
        if(type.contains("price")){
            conditionBuilder.or(qProduct.price.eq(keyword.toInt()))
        }

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

}