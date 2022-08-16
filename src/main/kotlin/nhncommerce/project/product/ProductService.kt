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
import java.io.File
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

    fun separate(productOptionDTO: ProductOptionDTO): MutableList<Any> {
        val category = categoryRepository.findById(productOptionDTO.categoryId!!.toLong()).get()
        val productDTO = ProductDTO(
            0L,
            Status.ACTIVE,
            productOptionDTO.productName,
            productOptionDTO.price,
            productOptionDTO.briefDescription,
            productOptionDTO.detailDescription,
            productOptionDTO.thumbnail,
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


    fun createProduct(productDTO: ProductDTO, inputSteam: InputStream): Product {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        val url = imageService.uploadImage(inputSteam)
        productDTO.thumbnail = url

        return productRepository.save(productDTO.dtoToEntity())
    }


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


    fun getProductList(pageRequestDTO: PageRequestDTO): PageResultDTO<ProductDTO, Product> {
        pageRequestDTO.size = 12
        val pageable = pageRequestDTO.getPageable(Sort.by("productId").descending())
        val booleanBuilder = getSearch(pageRequestDTO)
        val result = productRepository.findAll(booleanBuilder, pageable)

        val fn: Function<Product, ProductDTO> =
            Function<Product, ProductDTO> { entity: Product? -> entity?.entityToDto() }

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
        return product.entityToDto()
    }


    fun getThumbnailUUID(product: Product): String {
        val thumbnail = productRepository.findById(product.productId).get().thumbnail
        val thumbnailUUID = thumbnail.split("/").toTypedArray()
        return thumbnailUUID[6]
    }


    fun updateProduct(productDTO: ProductDTO, file : MultipartFile) {
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        val product = productRepository.findById(productDTO.productId).get()

        if(!file.originalFilename.equals("")){ //새이미지 들어오면
            val thumbnail = getThumbnailUUID(product)
            val url = imageService.uploadImage(file.inputStream)
            productDTO.thumbnail = url
            imageService.deleteImage(thumbnail)
        }else{ //기존 이미지 유지
            productDTO.thumbnail = product.thumbnail
        }
        product.updateProduct(productDTO)
        productRepository.save(product)
    }

    fun deleteProduct(productId: String) {
        val product = productRepository.findById(productId.toLong())
        product.get().status = Status.IN_ACTIVE
        productRepository.save(product.get())
    }


    fun getProductImageDTOList(product: Product): List<ProductImageDTO>? {
        val imageList = mutableListOf<ProductImageDTO>()
        val list = productImageRepository.findByProduct(product)?: return null
        for (productImage in list)
            imageList.add(ProductImageDTO(productImage.productImageId!!, productImage.image))
        return imageList
    }

    fun deleteProductImage(productImageId : Long){
        val getToken = storageTokenService.getTokenId()
        imageService.insertTokenId(getToken)

        val productImage = productImageRepository.findById(productImageId).get()
        imageService.deleteImage(productImage.image.split("/").toList().last())
        productImageRepository.delete(productImage)
    }

    fun getThumbnail(productId : String) : String{
        return productRepository.findByProductId(productId.toLong()).thumbnail
    }

}
