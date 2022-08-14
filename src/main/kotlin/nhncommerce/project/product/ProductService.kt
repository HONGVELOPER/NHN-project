package nhncommerce.project.product

import com.querydsl.core.BooleanBuilder
import nhncommerce.project.baseentity.Status
import nhncommerce.project.category.CategoryRepository
import nhncommerce.project.image.ImageService
import nhncommerce.project.option.domain.OptionListDTO
import nhncommerce.project.page.PageRequestDTO
import nhncommerce.project.page.PageResultDTO
import nhncommerce.project.product.domain.*
import nhncommerce.project.util.token.StorageTokenService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.function.Function

@Service
class ProductService(
    val productRepository: ProductRepository,
    val productImageRepository: ProductImageRepository,
    val categoryRepository: CategoryRepository,
    val imageService: ImageService,
    val storageTokenService: StorageTokenService
) {

    fun dtoTOEntity(productDTO: ProductDTO): Product {
        val product = Product(
            null, Status.ACTIVE, productDTO.productName, productDTO.price,
            productDTO.briefDescription, productDTO.detailDescription, productDTO.thumbnail, productDTO.viewCount,
            productDTO.totalStar, productDTO.category
        )

        return product
    }

    fun entityToDto(product: Product): ProductDTO {
        val productDTO = ProductDTO(
            product.productId, product.status, product.productName, product.price, product.briefDescription,
            product.briefDescription, product.thumbnail, product.viewCount, product.totalStar, product.category
        )
        return productDTO
    }

    fun separate(productOptionDTO: ProductOptionDTO): MutableList<Any> {
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
    fun createProduct(productDTO: ProductDTO, inputSteam: InputStream): Product {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        val url = imageService.uploadImage(inputSteam)
        productDTO.thumbnail = url

        val product = dtoTOEntity(productDTO)

        return productRepository.save(product)
    }

    /**
     * 상품 이미지 등록
     */
    fun createProductImageList(fileList: List<MultipartFile>, product: Product) {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        if (fileList.get(0).originalFilename.equals(""))
            return
        //상품 이미지 목록 저장
        for (file in fileList) {
            val imgUrl = imageService.uploadImage(file?.inputStream)
            val productImage = ProductImage(null, Status.ACTIVE, imgUrl, product)
            productImageRepository.save(productImage)
        }
    }

    /**
     * 상품 전제 조회
     */
    fun getProductList(pageRequestDTO: PageRequestDTO): PageResultDTO<ProductDTO, Product> {
        pageRequestDTO.size = 12
        val pageable = pageRequestDTO.getPageable(Sort.by("productId").descending())
        val booleanBuilder = getSearch(pageRequestDTO)
        val result = productRepository.findAll(booleanBuilder, pageable)

        val fn: Function<Product, ProductDTO> =
            Function<Product, ProductDTO> { entity: Product? -> entityToDto(entity!!) }

        return PageResultDTO<ProductDTO, Product>(result, fn)
    }

    fun getProduct(productId: Long): Product {
        return productRepository.findById(productId).get()
    }

    fun getProductDTO(productId: Long): ProductDTO {
        return productRepository.findById(productId).get().entityToDto()
    }

    fun getSearch(pageRequestDTO: PageRequestDTO): BooleanBuilder {

        val type = pageRequestDTO.type

        val booleanBuilder = BooleanBuilder()

        val qProduct = QProduct.product

        val keyword = pageRequestDTO.keyword

        val expression = qProduct.productId.gt(0L).and(qProduct.status.eq(Status.ACTIVE))

        booleanBuilder.and(expression)

        if (type.trim().isEmpty()) {
            return booleanBuilder
        }

        val conditionBuilder = BooleanBuilder()

        if (type.contains("productName")) {
            conditionBuilder.or(qProduct.productName.contains(keyword))
        }
        if (type.contains("price")) {
            conditionBuilder.or(qProduct.price.eq(keyword.toInt()))
        }

        booleanBuilder.and(conditionBuilder)
        return booleanBuilder
    }

    fun getProduct(productId: String): ProductDTO {
        val product = productRepository.findById(productId.toLong()).get()
        return entityToDto(product)
    }

    /**
     * 상품 이미지 삭제하기 위해 uuid파싱
     */
    fun getThumbnailUUID(product: Product): String {
        val thumbnail = productRepository.findById(product.productId!!).get().thumbnail
        val thumbnailUUID = thumbnail.toString().split("/").toTypedArray()
        return thumbnailUUID[6]
    }

    /**
     * 새 이미지 저장 후 기존 이미지의 uuid를 사용해 서버의 이미지 삭제
     */
    fun updateProduct(productDTO: ProductDTO, inputSteam: InputStream) {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        val product = productRepository.findById(productDTO.productId!!.toLong()).get()
        val thumbnail = getThumbnailUUID(product)
        val url = imageService.uploadImage(inputSteam)
        productDTO.thumbnail = url
        imageService.deleteImage(thumbnail)
        product.updateProduct(productDTO)
        productRepository.save(product)
    }

    fun deleteProduct(productId: String) {
        val product = productRepository.findById(productId.toLong())
        product.get().status = Status.IN_ACTIVE
        productRepository.save(product.get())
    }

    /**
     * 상품 이미지 dto 리스트 조회
     */
    fun getProductImageDTOList(product: Product): List<ProductImageDTO>? {
        val imageList = mutableListOf<ProductImageDTO>()
        val list = productImageRepository.findByProduct(product)?: return null
        for (productImage in list)
            imageList.add(ProductImageDTO(productImage.productImageId!!, productImage.image))
        return imageList
    }
    /**
     * 상품 이미지 삭제
     */
    fun deleteProductImage(productImageId : Long){
        productImageRepository.deleteById(productImageId)
    }
}
