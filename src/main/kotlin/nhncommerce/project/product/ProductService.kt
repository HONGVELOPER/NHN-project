package nhncommerce.project.product

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.CategoryRepository
import nhncommerce.project.image.ImageService
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.domain.Product
import nhncommerce.project.product.domain.ProductDTO
import nhncommerce.project.product.domain.ProductOptionDTO
import nhncommerce.project.product.domain.QProduct
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.function.Function

@Service
class ProductService(
    val productRepository: ProductRepository,
    val categoryRepository: CategoryRepository,
    val imageService: ImageService,
    val storageTokenService: StorageTokenService
) {

    fun dtoTOEntity(productDTO: ProductDTO) : Product{
        val product = Product(status = Status.ACTIVE, productName = productDTO.productName, price = productDTO.price,
                briefDescription = productDTO.briefDescription, detailDescription = productDTO.detailDescription,
                thumbnail = productDTO.thumbnail, viewCount = productDTO.viewCount, totalStar = productDTO.totalStar, category = productDTO.category)
        return product
    }

    fun entityToDto(product: Product) : ProductDTO{
        val productDTO = ProductDTO(product.productId,product.status, product.productName, product.price, product.briefDescription,
                                    product.briefDescription, product.thumbnail, product.viewCount, product.totalStar, product.category)
        return productDTO
    }

    fun separate(productOptionDTO: ProductOptionDTO) : MutableList<Any>{
        val category = categoryRepository.findById(productOptionDTO.categoryId!!.toLong()).get()
        val productDTO = ProductDTO(
            null,
            Status.ACTIVE,
            productOptionDTO.productName,
            productOptionDTO.price,
            productOptionDTO.briefDescription,
            productOptionDTO.detailDescription,
            productOptionDTO.thumbnail,
            productOptionDTO.viewCount,
            productOptionDTO.totalStar,
            category,
        )
        val optionListDTO = OptionListDTO(
            productDTO,
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

    /**
     * 상품 등록시 사진 넣지 않아면 thumbnail에 빈문자열 들어감
     */
    fun createProduct(productDTO: ProductDTO, inputSteam: InputStream) : Product{
        println("==========================$imageService.tokenId")
        val url = imageService.uploadImage(inputSteam)
        productDTO.thumbnail=url

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

    fun getProductDTO(productId : Long) : ProductDTO {
        return productRepository.findById(productId).get().toProductDTO()
    }

    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

        var type = pageRequestDTO.type

        var booleanBuilder = BooleanBuilder()

        var qProduct = QProduct.product

        var keyword = pageRequestDTO.keyword

        var expression = qProduct.productId.gt(0L).and(qProduct.status.eq(Status.ACTIVE))

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

    fun getProduct(productId : String) : ProductDTO{
        val product = productRepository.findById(productId.toLong()).get()
        return entityToDto(product)
    }

    /**
     * 상품 이미지 삭제하기 위해 uuid파싱
     */
    fun getThumbnailUUID(product : Product) : String{
        val thumbnail = productRepository.findById(product.productId!!).get().thumbnail
        var thumbnailUUID = thumbnail.toString().split("/").toTypedArray()
        return thumbnailUUID[6]
    }

    /**
     * 새 이미지 저장 후 기존 이미지의 uuid를 사용해 서버의 이미지 삭제
     */
    fun updateProduct(productDTO: ProductDTO, inputSteam: InputStream){
        var product = productRepository.findById(productDTO.productId!!.toLong()).get()
        var thumbnail = getThumbnailUUID(product)
        val url = imageService.uploadImage(inputSteam)
        productDTO.thumbnail=url
        imageService.deleteImage(thumbnail)
        product.updateProduct(productDTO)
        productRepository.save(product)
    }

    fun deleteProduct(productId : String){
        var product = productRepository.findById(productId.toLong())
        product.get().status=Status.IN_ACTIVE
        productRepository.save(product.get())
    }

    fun generateToken(){
        when(storageTokenService.hasToken()){
            true ->  storageTokenService.checkExpired()
            false -> storageTokenService.generateToken()
        }
    }
}